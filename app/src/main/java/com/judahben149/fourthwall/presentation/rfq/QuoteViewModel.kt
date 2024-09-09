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


//    fun parsePaymentMethodFields() {
//        try {
//            state.value.selectedOffering?.let { off ->
//                // Use the selected payment kind to parse out the fields
//
//                state.value.selectedPaymentKind?.let { pair ->
//                    val selectedKind = off.data.payin.methods.find { it.kind == pair.first }
//
//                    // Begin to traverse the selected payment kind to parse out the fields
//                    selectedKind?.let { payMethod ->
//                        // start with Pay In fields
//                        val payInFields = emptyMap<Int, PayFields>()
//
//                        payMethod.parseRequiredPaymentDetails()?.let { details ->
//
//                        }
//                    }
//                }
//            }
//        } catch (ex: Exception) {
//            //Show graceful error message
//        }
//    }

    fun updateSelectedOffering(offering: Offering) {
        _state.update { it ->
            it.copy(
                selectedOffering = offering,
                paymentKinds = offering.data.payin.methods.map {
                    PaymentKind(it.kind.formatKindEnum(), it.kind.formatKind(), false)
                }
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
        return when {
            this.contains("usd_bank", true) -> "USD Transfer"
            this.contains("wallet", true) -> "Wallet Address"
            this.contains("transfer", true) -> "Transfer"
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
    val formattedKind: String,
    val isSelected: Boolean,

    val walletAddress: String = "",
    val bankAccountPayIn: String = "",
    val bankAccountPayOut: String = "",
    val bankAccountPayInUsd: String = "",
    val bankAccountPayOutUsd: String = "",
    val routingNumber: String = "",
)