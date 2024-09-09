package com.judahben149.fourthwall.presentation.rfq

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.judahben149.fourthwall.domain.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.web3j.abi.datatypes.Bool
import tbdex.sdk.protocol.models.Offering
import tbdex.sdk.protocol.models.PaymentMethod
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(QuoteState())
    val state: StateFlow<QuoteState> = _state


    fun parsePaymentMethodFields() {
        try {
            state.value.selectedOffering?.let { off ->
                // Use the selected payment kind to parse out the fields

                state.value.selectedPaymentKind?.let { pair ->
                    val selectedKind = off.data.payin.methods.find { it.kind == pair.first }

                    // Begin to traverse the selected payment kind to parse out the fields
                    selectedKind?.let { payMethod ->
                        // start with Pay In fields
                        val payInFields = emptyMap<Int, PayFields>()

                        payMethod.parseRequiredPaymentDetails()?.let { details ->

                        }
                    }
                }
            }
        } catch (ex: Exception) {
            //Show graceful error message
        }
    }

    fun updateSelectedOffering(offering: Offering) {
        _state.update { it ->
            it.copy(
                selectedOffering = offering,
                possiblePaymentKinds = offering.data.payin.methods.map {
                    PossiblePaymentKind(it.kind, it.kind.formatKind(), false)
                }
            )
        }
    }

    fun updateSelectedPaymentKind(paymentKind: Pair<String, String>) {
        _state.update {
            it.copy(
                selectedPaymentKind = paymentKind
            )
        }
    }


    private fun String.formatKind(): String {
        return when {
            this.contains("transfer", true) -> "Transfer"
            this.contains("wallet", true) -> "Wallet Address"
            this.contains("address", true) -> "Wallet Address"
            this.contains("debit", true) -> "Debit Card"
            this.contains("credit", true) -> "Credit Card"
            else -> this
        }
    }

    private fun PaymentMethod.parseRequiredPaymentDetails(): RequiredPaymentDetails? {
        val gson = Gson()

        return try {
            val jsonObject =
                gson.fromJson(requiredPaymentDetails.toString(), JsonObject::class.java)
            val propertiesObject = jsonObject.getAsJsonObject("properties")

            // IMPORTANT - The payment method details fields should be updated here upon integrating
            // to a new PFI with newer payment methods
            val details = Details(
                accountNumber = null,
                routingNumber = null,
                walletAddress = null
            )

            propertiesObject.keySet().forEach { key ->
                when (key) {
                    "accountNumber" -> details.accountNumber = ""
                    "routingNumber" -> details.routingNumber = ""
                    "walletAddress" -> details.walletAddress = ""
                    // Add more fields here as said earlier
                }
            }

            RequiredPaymentDetails(kind, details)
        } catch (e: Exception) {
            null
        }
    }

    fun confirmCredentials(): Boolean {
        return !sessionManager.getKCC("").isNullOrEmpty()
    }
}

data class QuoteState(
    val selectedOffering: Offering? = null,
    val possiblePaymentKinds: List<PossiblePaymentKind> = emptyList(),
    val selectedPaymentKind: Pair<String, String>? = null,
    val paymentMethodFields: PaymentMethodFields = PaymentMethodFields()
)

data class PaymentMethodFields(
    val payInfields: Map<Int, PayFields> = emptyMap(),
    val payOutfields: Map<Int, PayFields> = emptyMap()
)

data class PayFields(
    val fieldTitle: String = "",
    val fieldDescription: String = "",
    val inputType: String = "",
    val fieldHint: String = "",
    val value: String = ""
)

data class PossiblePaymentKind(
    val kind: String,
    val formattedKind: String,
    val isSelected: Boolean
)

data class Details(
    var accountNumber: String? = null,
    var routingNumber: String? = null,
    var walletAddress: String? = null
)

data class RequiredPaymentDetails(
    val kind: String,
    val details: Details
)