package com.judahben149.fourthwall.presentation.rfq

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.domain.models.enums.PaymentMethods
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
        _state.update { state ->
            val availablePaymentKindsList = mutableListOf<PaymentKind>()

            offering.data.payin.methods.forEachIndexed { methodIndex, payInMethod ->
                val payOutMethodCorresponding = offering.data.payout.methods[methodIndex]

                val newPaymentKind = PaymentKind(
                    kind = payInMethod.kind.formatKindEnum(),
                    formattedKindName = payInMethod.kind.formatKind(),
                    actualPayInKindName = payInMethod.kind,
                    actualPayOutKindName = payOutMethodCorresponding.kind,
                    payInMethod = payInMethod.kind.formatKindEnum().name,
                    payOutMethod = payOutMethodCorresponding.kind.formatKindEnum().name,
                    isSelected = false
                )

                availablePaymentKindsList.add(newPaymentKind)
            }

            state.copy(
                selectedOffering = offering,
                paymentKinds = availablePaymentKindsList
            )
        }
        btnDisabled()
    }


    fun updateSelectedPaymentKind(paymentKind: PaymentKind) {
        _state.update {
            it.copy(
                selectedPaymentKind = paymentKind
            )
        }
        btnEnabled()
    }

    fun requestForQuote() {
        btnLoading()

        viewModelScope.launch(Dispatchers.IO) {

            val rfqData = createRfq()
            val rfq: Rfq

            state.value.selectedOffering?.let { off ->
                rfqData?.let { data ->
                    rfq = Rfq.create(
                        to = off.metadata.from,
                        from = sessionManager.getDid() ?: "",
                        rfqData = data
                    )

                    val rfqVerification = verifyRfq(rfq)
                    rfq.log("RFQ HERE -----> ")
                    rfqVerification.log("VERIFICATION ----> ")

                    sessionManager.getBearerDid()?.let { bearerDid ->
                        try {
                            rfq.sign(bearerDid)
                            TbdexHttpClient.createExchange(rfq)

                            _state.update {
                                it.copy(
                                    exchangeProgress = ExchangeProgress.HasRequestedQuote
                                )
                            }

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
            rfq.verifyOfferingRequirements(state.value.selectedOffering!!)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun createRfq(): CreateRfqData? {
        return state.value.selectedPaymentKind?.let { paymentKind ->

            val payInData = CreateSelectedPayinMethod(
                kind = paymentKind.actualPayInKindName,
                paymentDetails = parseRequiredPayInDetails(paymentKind.actualPayInKindName.formatKindEnum()),
                amount = state.value.amount ?: ""
            )


            val payOutData = CreateSelectedPayoutMethod(
                kind = paymentKind.actualPayOutKindName,
                paymentDetails = parseRequiredPayOutDetails(paymentKind.actualPayOutKindName.formatKindEnum())
            )

            val rfqData = CreateRfqData(
                offeringId = state.value.selectedOffering?.metadata?.id ?: "",
                payin = payInData,
                payout = payOutData,
                claims = listOf(sessionManager.getKCC("")!!)
            )

            rfqData
        }
    }

    // Remove
    private fun parseRequiredPayDetails(isPayIn: Boolean): Map<String, Any> {

        val detailsMap = mutableMapOf<String, Any>()

        state.value.selectedPaymentKind?.let { kind ->
            if (isPayIn) {
                when (kind.kind) {
                    PaymentMethods.WALLET_ADDRESS -> {
                        detailsMap.put("address", kind.payInWalletAddress)
                    }

                    PaymentMethods.BANK_TRANSFER -> {
                        detailsMap.put("accountNumber", kind.payInBankAccount)
                    }

                    PaymentMethods.BANK_TRANSFER_USD -> {
                        detailsMap.put("accountNumber", kind.payInBankAccount)
                        detailsMap.put("routingNumber", kind.payInRoutingNumber)
                    }
                }
            } else {
                when (kind.kind) {
                    PaymentMethods.WALLET_ADDRESS -> {
                        detailsMap.put("address", kind.payOutWalletAddress)
                    }

                    PaymentMethods.BANK_TRANSFER -> {
                        detailsMap.put("accountNumber", kind.payOutBankAccount)
                    }

                    PaymentMethods.BANK_TRANSFER_USD -> {
                        detailsMap.put("accountNumber", kind.payOutBankAccount)
                        detailsMap.put("routingNumber", kind.payOutRoutingNumber)
                    }
                }
            }
        }

        return detailsMap
    }

    // Remove
    private fun parseRequiredPayDetails2(isPayIn: Boolean): Map<String, Any> {
        return state.value.selectedPaymentKind?.let { kind ->

            buildMap {
                when (kind.kind) {
                    PaymentMethods.WALLET_ADDRESS -> {
                        put(
                            "address",
                            if (isPayIn) kind.payInWalletAddress else kind.payOutWalletAddress
                        )
                    }

                    PaymentMethods.BANK_TRANSFER -> {
                        put(
                            "accountNumber",
                            if (isPayIn) kind.payInBankAccount else kind.payOutBankAccount
                        )
                    }

                    PaymentMethods.BANK_TRANSFER_USD -> {
                        put(
                            "accountNumber",
                            if (isPayIn) kind.payInBankAccount else kind.payOutBankAccount
                        )
                        put(
                            "routingNumber",
                            if (isPayIn) kind.payInRoutingNumber else kind.payOutRoutingNumber
                        )
                    }

                    else -> {
                        // Handle unexpected payment methods
                        // You might want to log a warning or throw an exception here
                    }
                }
            }
        } ?: emptyMap()
    }


    private fun parseRequiredPayInDetails(method: PaymentMethods): Map<String, Any> {
        val detailsMap = mutableMapOf<String, Any>()

        state.value.selectedPaymentKind?.let { kind ->
            when (method) {
                PaymentMethods.WALLET_ADDRESS -> {
                    detailsMap.put("address", kind.payInWalletAddress)
                }

                PaymentMethods.BANK_TRANSFER -> {
                    detailsMap.put("accountNumber", kind.payInBankAccount)
                }

                PaymentMethods.BANK_TRANSFER_USD -> {
                    detailsMap.put("accountNumber", kind.payInBankAccount)
                    detailsMap.put("routingNumber", kind.payInRoutingNumber)
                }
            }
        }

        return detailsMap
    }

    private fun parseRequiredPayOutDetails(method: PaymentMethods): Map<String, Any> {
        val detailsMap = mutableMapOf<String, Any>()

        state.value.selectedPaymentKind?.let { kind ->
            when (method) {
                PaymentMethods.WALLET_ADDRESS -> {
                    detailsMap.put("address", kind.payOutWalletAddress)
                }

                PaymentMethods.BANK_TRANSFER -> {
                    detailsMap.put("accountNumber", kind.payOutBankAccount)
                }

                PaymentMethods.BANK_TRANSFER_USD -> {
                    detailsMap.put("accountNumber", kind.payOutBankAccount)
                    detailsMap.put("routingNumber", kind.payOutRoutingNumber)
                }
            }
        }

        return detailsMap
    }

    private fun String.formatKind(): String {
        val sanitizedString = this.trim().lowercase()

        return when {
            sanitizedString.contains("wallet") -> "Wallet Address"
            sanitizedString.contains("transfer") -> "Bank Transfer"
            else -> this
        }
    }


    private fun String.formatKindEnum(): PaymentMethods {
        return when {
            this.contains("usd_bank", true) -> PaymentMethods.BANK_TRANSFER_USD
            this.contains("wallet", true) -> PaymentMethods.WALLET_ADDRESS
            else -> PaymentMethods.BANK_TRANSFER
        }
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
    val selectedOffering: Offering? = null,
    val paymentKinds: List<PaymentKind> = emptyList(),
    val selectedPaymentKind: PaymentKind? = null,
    val isQuoteReceived: Boolean = false,
    val btnState: QuoteButtonState = QuoteButtonState.Disabled,
    val exchangeProgress: ExchangeProgress = ExchangeProgress.YetToRequestQuote
)

data class PaymentKind(
    val kind: PaymentMethods,
    val formattedKindName: String,
    val actualPayInKindName: String,
    val actualPayOutKindName: String,
    val isSelected: Boolean,
    var payInMethod: String = "",
    var payInBankAccount: String = "",
    var payInRoutingNumber: String = "",
    var payInWalletAddress: String = "",
    var payOutMethod: String = "",
    var payOutBankAccount: String = "",
    var payOutRoutingNumber: String = "",
    var payOutWalletAddress: String = ""
)

sealed class ExchangeProgress {
    data object YetToRequestQuote : ExchangeProgress()
    data object HasRequestedQuote : ExchangeProgress()
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