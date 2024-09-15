package com.judahben149.fourthwall.presentation.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.fourthwall.domain.mappers.toCurrency
import com.judahben149.fourthwall.domain.models.Currency
import com.judahben149.fourthwall.domain.models.PfiData
import com.judahben149.fourthwall.domain.usecase.pfi.GetPfiDataUseCase
import com.judahben149.fourthwall.domain.usecase.pfi.GetPfiOfferingsUseCase
import com.judahben149.fourthwall.domain.usecase.pfiRating.GetAllAveragePfiRatingsUseCase
import com.judahben149.fourthwall.domain.usecase.pfiRating.GetAveragePfiRatingUseCase
import com.judahben149.fourthwall.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tbdex.sdk.protocol.models.Offering
import javax.inject.Inject

@HiltViewModel
class OfferingsViewModel @Inject constructor(
    private val getPfiDataUseCase: GetPfiDataUseCase,
    private val getPfiOfferingsUseCase: GetPfiOfferingsUseCase,
    private val getAllAveragePfiRatingsUseCase: GetAllAveragePfiRatingsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OfferingsFlowState())
    val state: StateFlow<OfferingsFlowState> = _state

    init {
        getPfiOfferings()
    }

    fun getPfiOfferings() {
        updateBtnState(OfferingsBtnState.Loading)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _state.update {
                    it.copy(getOfferingsState = GetOfferingsRequestState.Loading)
                }

                val pfiData = getPfiDataUseCase()

                // Store PFI Info in memory
                Constants.pfiData = pfiData.associate { it.pfiDid to it.pfiName }

                val offeringsList = pfiData.map { pfi ->
                    async {
                        try {
                            getPfiOfferingsUseCase(pfi)
                        } catch (e: Exception) {
                            _state.update { it.copy(getOfferingsState = GetOfferingsRequestState.Error(
                                "Error fetching offerings, please retry"
                            )) }

                            emptyList()
                        }
                    }
                }.awaitAll()

                val flatOfferingsList = offeringsList.flatten()

                if (flatOfferingsList.isNotEmpty()) {

                    _state.update {
                        it.copy(
                            pfiData = pfiData,
                            getOfferingsState = GetOfferingsRequestState.Success,
                            offeringsList = flatOfferingsList,
                            supportedCurrencyPairs = createCurrencyPairs(flatOfferingsList),
                            supportedPayInCurrencies = parseSupportedPayInCurrencies(
                                flatOfferingsList
                            ),
                            supportedPayOutCurrencies = parseSupportedPayOutCurrencies(
                                flatOfferingsList
                            ),
                        )
                    }

                    state.value.run {
                        if (selectedOffering != null && selectedPayInCurrency != null && selectedPayOutCurrency != null) {
                            updateBtnState(OfferingsBtnState.Enabled())
                        } else {
                            updateBtnState(OfferingsBtnState.Disabled())
                        }
                    }
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
                _state.update {
                    it.copy(
                        getOfferingsState = GetOfferingsRequestState.Error(
                            ex.message ?: "An error occurred"
                        )
                    )
                }

                updateBtnState(OfferingsBtnState.Disabled())
            }
        }
    }

    private fun parseSupportedPayInCurrencies(offeringsList: List<Offering>): List<Currency> {
        val payInCurrencyList = mutableListOf<Currency>()

        offeringsList.forEach { offering ->
            offering.data.payin.currencyCode.toCurrency()?.let { payInCurrencyList.add(it) }
        }

        return payInCurrencyList
    }

    private fun parseSupportedPayOutCurrencies(offeringsList: List<Offering>): List<Currency> {
        val payOutCurrencyList = mutableListOf<Currency>()

        offeringsList.forEach { offering ->
            offering.data.payout.currencyCode.toCurrency()?.let { payOutCurrencyList.add(it) }
        }

        return payOutCurrencyList
    }

    fun updateSelectedPayInCurrency(currencyCode: String) {
        currencyCode.toCurrency()?.let { currency ->
            _state.update { state ->
                state.copy(selectedPayInCurrency = currency)
            }
        }
    }

    fun updateSelectedPayOutCurrency(currencyCode: String?) {
        currencyCode?.toCurrency()?.let { currency ->
            _state.update { state ->
                state.copy(selectedPayOutCurrency = currency)
            }
        }

        refreshPayOutInfo()
    }

    private fun createCurrencyPairs(offeringsList: List<Offering>): List<Pair<Currency, Currency>> {
        val currencyPairsList = mutableListOf<Pair<Currency, Currency>>()

        offeringsList.forEach { offering ->
            val payInCurrency = offering.data.payin.currencyCode.toCurrency()
            val payOutCurrency = offering.data.payout.currencyCode.toCurrency()

            payInCurrency?.let { payIn ->
                payOutCurrency?.let { payOut ->
                    currencyPairsList.add(Pair(payIn, payOut))
                }
            }
        }

        return currencyPairsList
    }

    private fun refreshPayOutAmountState() {

        state.value.run {
            if (selectedOffering == null) {
                _state.update {
                    it.copy(
                        payOutAmountState = PayOutAmountState.Inactive("0.0")
                    )
                }
                return
            }

            if (selectedPayInCurrency == null) {
                _state.update {
                    it.copy(
                        payOutAmountState = PayOutAmountState.Error("Select pay in currency")
                    )
                }
                return
            }

            if (selectedPayOutCurrency == null) {
                _state.update {
                    it.copy(
                        payOutAmountState = PayOutAmountState.Error("Select pay out currency")
                    )
                }
                return
            }

            if (payInAmount.isNullOrEmpty()) {
                _state.update {
                    it.copy(
                        payOutAmountState = PayOutAmountState.Error("Enter pay in amount")
                    )
                }
                return
            }
        }


        calculateExchangeRate()
    }

    private fun calculateExchangeRate() {

        state.value.run {
            payInAmount?.let { payInAmount ->
                selectedOffering?.data?.payoutUnitsPerPayinUnit?.let { units ->
                    try {
                        // Add in FourthWall fee here (1.2% flat fee)
                        val payoutAmount = (payInAmount.toDouble()) * units.toDouble()
//                        val payoutAmountCharged = payoutAmount - (payoutAmount * (1.2 / 100))

                        val payoutAmountFormatted = String.format("%.2f", payoutAmount)

                        _state.update {
                            it.copy(
                                payOutAmountState = PayOutAmountState.Available(
                                    payoutAmountFormatted
                                )
                            )
                        }

                        updateBtnState(OfferingsBtnState.Enabled())

                    } catch (ex: Exception) {
                        _state.update {
                            it.copy(
                                payOutAmountState = PayOutAmountState.Error("Invalid entry")
                            )
                        }

                        updateBtnState(OfferingsBtnState.Disabled())
                    }

                }
            }
        }
    }

    private fun pickBestOffering() {
        invalidateOfferingSelection()

        state.value.run {
            selectedPayInCurrency?.let { payIn ->
                selectedPayOutCurrency?.let { payOut ->
                    val validOfferings = offeringsList.filter {
                        (it.data.payin.currencyCode == payIn.code) &&
                                (it.data.payout.currencyCode == payOut.code)
                    }

                    val bestOffering =
                        validOfferings.maxByOrNull { it.data.payoutUnitsPerPayinUnit }

                    _state.update {
                        it.copy(
                            selectedOffering = bestOffering,
                            isBestOfferSelected = validOfferings.size > 1
                        )
                    }
                }
            }
        }

        refreshPayOutAmountState()
    }

    private fun pickOtherOffering() {
        invalidateOfferingSelection()
    }

    private fun invalidateOfferingSelection() {
        _state.update {
            it.copy(
                selectedOffering = null,
                isBestOfferSelected = null
            )
        }
    }

    fun updateSelectedOffering(offeringId: String) {
        invalidateOfferingSelection()
        val offering = state.value.offeringsList.find { it.metadata.id == offeringId }

        offering?.let {
            _state.update { state ->
                state.copy(
                    selectedOffering = offering
                )
            }
        }
    }

    fun refreshPayOutInfo() {
        pickBestOffering()
    }

    fun updatePayInAmount(amount: String) {
        _state.update { it.copy(payInAmount = amount) }
        updateBtnState(OfferingsBtnState.Loading)
        pickBestOffering()
    }

    private fun updateBtnState(state: OfferingsBtnState) {
        _state.update { it.copy(btnState = state) }
    }

    fun getSelectedPfiName(): String? {
        return state.value.run {
            selectedOffering?.let { offering ->
                pfiData.first { pfiData ->
                    pfiData.pfiDid == offering.metadata.from
                }.pfiName
            }
        }
    }

    private fun getPfiNameFromOfferingId(offeringId: String): String {
        val offering = state.value.offeringsList.find { it.metadata.id == offeringId }

        return offering?.let { off ->
            val pfiName = state.value.pfiData.find { it.pfiDid == off.metadata.from }?.pfiName ?: ""
            pfiName
        } ?: ""
    }

    fun pairOfferingsWithPfiNames(): List<Pair<String, Offering>> {
        val selectedPayInCurrency = state.value.selectedPayInCurrency
        val selectedPayOutCurrency = state.value.selectedPayOutCurrency

        if (selectedPayInCurrency == null || selectedPayOutCurrency == null) return emptyList()

        val offeringList = state.value.offeringsList.filter { off ->
            (off.data.payin.currencyCode == selectedPayInCurrency.code) &&
                    (off.data.payout.currencyCode == selectedPayOutCurrency.code)
        }.distinct()

        val pairsList = mutableListOf<Pair<String, Offering>>()

        offeringList.forEach { off ->
            val pfiName = getPfiNameFromOfferingId(off.metadata.id)
            pairsList.add(Pair(pfiName, off))
        }

        return pairsList
    }

    suspend fun getAveragePfiRating(): Map<String, Double> = getAllAveragePfiRatingsUseCase()
}

data class OfferingsFlowState(
    val pfiData: List<PfiData> = emptyList(),
    val selectedPayInCurrency: Currency? = null,
    val selectedPayOutCurrency: Currency? = null,
    val payInAmount: String? = null,
    val btnState: OfferingsBtnState = OfferingsBtnState.Loading,
    val payOutAmountState: PayOutAmountState = PayOutAmountState.Inactive("0.0"),
    val selectedOffering: Offering? = null,
    val isBestOfferSelected: Boolean? = false,
    val userDisregardsBestOffer: Boolean = false,
    val supportedCurrencyPairs: List<Pair<Currency, Currency>> = emptyList(),
    val supportedPayInCurrencies: List<Currency> = emptyList(),
    val supportedPayOutCurrencies: List<Currency> = emptyList(),
    val isPayoutFieldEnabled: Boolean = false,
    val getOfferingsState: GetOfferingsRequestState = GetOfferingsRequestState.Loading,
    val offeringsList: List<Offering> = emptyList()
)


sealed class GetOfferingsRequestState {
    data object Loading : GetOfferingsRequestState()
    data object Success : GetOfferingsRequestState()
    data class Error(val message: String) : GetOfferingsRequestState()
}

sealed class PayOutAmountState {
    data class Available(val amount: String) : PayOutAmountState()
    data class Inactive(val message: String) : PayOutAmountState()
    data class Error(val message: String) : PayOutAmountState()
}

sealed class OfferingsBtnState {
    data object Loading : OfferingsBtnState()
    data class Enabled(val message: String = "Continue") : OfferingsBtnState()
    data class Disabled(val error: String = "") : OfferingsBtnState()
}