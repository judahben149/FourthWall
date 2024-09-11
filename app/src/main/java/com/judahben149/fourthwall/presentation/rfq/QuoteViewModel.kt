package com.judahben149.fourthwall.presentation.rfq

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.domain.mappers.toFwOffering
import com.judahben149.fourthwall.domain.models.FWOffering
import com.judahben149.fourthwall.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tbdex.sdk.httpclient.TbdexHttpClient
import tbdex.sdk.protocol.models.Close
import tbdex.sdk.protocol.models.CreateRfqData
import tbdex.sdk.protocol.models.CreateSelectedPayinMethod
import tbdex.sdk.protocol.models.CreateSelectedPayoutMethod
import tbdex.sdk.protocol.models.Offering
import tbdex.sdk.protocol.models.Quote
import tbdex.sdk.protocol.models.Rfq
import web5.sdk.dids.did.BearerDid
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(QuoteState())
    val state: StateFlow<QuoteState> = _state

    fun updateAmount(amount: String) {
        _state.update { it.copy(amount = amount) }
    }

    fun updateSelectedOffering(offering: Offering) {

        _state.update {
            it.copy(
                tbDexOffering = offering,
                fwOffering = offering.toFwOffering(),
                exchangeProgress = ExchangeProgress.YetToRequestQuote
            )
        }
    }

    fun requestForQuote() {
        _state.update { it.copy(exchangeProgress = ExchangeProgress.HasRequestedQuote) }

        viewModelScope.launch(Dispatchers.IO) {

            val rfqData = createRfq()
            val rfq: Rfq

            state.value.fwOffering?.let { off ->

                    rfq = Rfq.create(
                        to = off.pfiDid,
                        from = sessionManager.getDid() ?: "",
                        rfqData = rfqData
                    )

                    val rfqVerification = verifyRfq(rfq)
                    rfq.log("RFQ HERE -----> ")
                    rfqVerification.log("VERIFICATION ----> ")

                    sessionManager.getBearerDid()?.let { bearerDid ->
                        try {
                            rfq.sign(bearerDid)
                            TbdexHttpClient.createExchange(rfq)


                            // Start polling for results
            //                            beginPollingForQuoteResponse(rfq, bearerDid)
                        } catch (ex: Exception) {
                            _state.update {
                                it.copy(
                                    exchangeProgress = ExchangeProgress.ErrorRequestingQuote(
                                        ex.message.toString()
                                    )
                                )
                            }
                            btnDisabled()
                        }
                }
            }
        }
    }

    private fun beginPollingForQuoteResponse(rfq: Rfq, bearerDid: BearerDid) {
        var quote: Quote? = null
        var close: Close? = null

//Wait for Quote message to appear in the exchange
        while (quote == null) {
            val exchange = TbdexHttpClient.getExchange(
                pfiDid = rfq.metadata.to,
                requesterDid = bearerDid,
                exchangeId = rfq.metadata.exchangeId
            )

            quote = exchange.find { it is Quote } as Quote?

            if (quote == null) {
                // Make sure the exchange is still open
                close = exchange.find { it is Close } as Close?

                if (close != null) {
                    break
                } else {
                    // Wait 2 seconds before making another request
                    Thread.sleep(2000)
                }
            }
        }

        quote?.let {
            it.toString().log("QUOTE HERE ---->")
        }
    }

    private fun verifyRfq(rfq: Rfq): Boolean {
        return try {
            rfq.verifyOfferingRequirements(state.value.tbDexOffering!!)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun createRfq(): CreateRfqData {

        val payInData = CreateSelectedPayinMethod(
            kind = state.value.payInKind,
            paymentDetails = state.value.payInRfqRequestFields,
            amount = state.value.amount ?: ""
        )


        val payOutData = CreateSelectedPayoutMethod(
            kind = state.value.payOutKind,
            paymentDetails = state.value.payOutRfqRequestFields
        )

        val rfqData = CreateRfqData(
            offeringId = state.value.tbDexOffering?.metadata?.id ?: "",
            payin = payInData,
            payout = payOutData,
            claims = listOf(sessionManager.getKCC("")!!)
        )

        return rfqData
    }

    fun confirmCredentials(): Boolean {
        return !sessionManager.getKCC("").isNullOrEmpty()
    }

    fun btnLoading() {
        changeBtnState(QuoteButtonState.Loading)
    }

    fun btnEnabled() {
        changeBtnState(QuoteButtonState.Enabled)
    }

    fun btnDisabled() {
        changeBtnState(QuoteButtonState.Disabled)
    }

    private fun changeBtnState(btnState: QuoteButtonState) {
        _state.update {
            it.copy(btnState = btnState)
        }
    }
}

data class QuoteState(
    val amount: String? = null,
    val fwOffering: FWOffering? = null,
    val tbDexOffering: Offering? = null,
    val btnState: QuoteButtonState = QuoteButtonState.Disabled,
    val exchangeProgress: ExchangeProgress = ExchangeProgress.JustStarted,
    val payInKind: String = "",
    val payOutKind: String = "",
    val payInRfqRequestFields: Map<String, Any> = emptyMap(),
    val payOutRfqRequestFields: Map<String, Any> = emptyMap(),
)

sealed class ExchangeProgress {
    data object JustStarted : ExchangeProgress()
    data object YetToRequestQuote : ExchangeProgress()
    data object IsReadyToRequestQuote : ExchangeProgress()
    data object HasRequestedQuote : ExchangeProgress()
    data object IsPollingForQuoteResponse : ExchangeProgress()
    data object HasGottenQuoteResponse : ExchangeProgress()
    data class ErrorRequestingQuote(val message: String) : ExchangeProgress()
    data object HasMadeOrder : ExchangeProgress()
    data object HasGottenOrderResponse : ExchangeProgress()
}

sealed class QuoteButtonState {
    data object Enabled : QuoteButtonState()
    data object Disabled : QuoteButtonState()
    data object Loading : QuoteButtonState()
}