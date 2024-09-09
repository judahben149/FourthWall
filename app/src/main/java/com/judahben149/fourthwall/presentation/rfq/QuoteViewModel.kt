package com.judahben149.fourthwall.presentation.rfq

import androidx.lifecycle.ViewModel
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.domain.models.enums.PaymentMethods
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import tbdex.sdk.protocol.models.Offering
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(QuoteState())
    val state: StateFlow<QuoteState> = _state


    fun updateSelectedOffering(offering: Offering) {
        _state.update { state ->
            val availablePaymentKindsList = mutableListOf<PaymentKind>()

            offering.data.payin.methods.forEachIndexed { methodIndex, payInMethod ->
                val payOutMethodCorresponding = offering.data.payout.methods[methodIndex]

                val newPaymentKind = PaymentKind(
                    kind = payInMethod.kind.formatKindEnum(),
                    formattedKindName = payInMethod.kind.formatKind(),
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
    val selectedOffering: Offering? = null,
    val paymentKinds: List<PaymentKind> = emptyList(),
    val selectedPaymentKind: PaymentKind? = null
)

data class PaymentKind(
    val kind: PaymentMethods,
    val formattedKindName: String,
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