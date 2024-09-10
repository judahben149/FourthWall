package com.judahben149.fourthwall.presentation.rfq

import androidx.lifecycle.ViewModel
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.domain.models.enums.PaymentMethods
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import tbdex.sdk.protocol.models.CreateRfqData
import tbdex.sdk.protocol.models.CreateSelectedPayinMethod
import tbdex.sdk.protocol.models.CreateSelectedPayoutMethod
import tbdex.sdk.protocol.models.Offering
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
    }


    fun updateSelectedPaymentKind(paymentKind: PaymentKind) {
        _state.update {
            it.copy(
                selectedPaymentKind = paymentKind
            )
        }
    }

    fun requestForQuote() {
        val rfqData = createRfq()
        val rfq: Rfq

        state.value.selectedOffering?.let { off ->
            rfqData?.let { data ->
                rfq = Rfq.create(
                    to = off.metadata.from,
                    from = sessionManager.getDid() ?: "",
                    rfqData = data
                )


            }
        }


    }

    private fun createRfq(): CreateRfqData? {
        return state.value.selectedPaymentKind?.let { paymentKind ->
            val payInData = CreateSelectedPayinMethod(
                kind = paymentKind.actualPayInKindName,
                paymentDetails = parseRequiredPayDetails(true),
                amount = state.value.amount ?: ""
            )


            val payOutData = CreateSelectedPayoutMethod(
                kind = paymentKind.actualPayOutKindName,
                paymentDetails = parseRequiredPayDetails(false)
            )

            val rfqData = CreateRfqData(
                offeringId = state.value.selectedOffering?.metadata?.id ?: "",
                payin = payInData,
                payout = payOutData,
                claims = listOf()
            )

            rfqData
        }
    }

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
}

data class QuoteState(
    val amount: String? = null,
    val selectedOffering: Offering? = null,
    val paymentKinds: List<PaymentKind> = emptyList(),
    val selectedPaymentKind: PaymentKind? = null,
    val isQuoteReceived: Boolean = false
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