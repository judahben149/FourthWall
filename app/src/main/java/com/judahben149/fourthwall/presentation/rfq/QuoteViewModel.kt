package com.judahben149.fourthwall.presentation.rfq

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.domain.mappers.toFwOffering
import com.judahben149.fourthwall.domain.mappers.toFwOrderEntity
import com.judahben149.fourthwall.domain.mappers.toFwPaymentMethod
import com.judahben149.fourthwall.domain.models.FWOffering
import com.judahben149.fourthwall.domain.models.FwOrderResult
import com.judahben149.fourthwall.domain.models.enums.FwOrderStatus
import com.judahben149.fourthwall.domain.models.enums.OrderType
import com.judahben149.fourthwall.domain.models.enums.PaymentMethods
import com.judahben149.fourthwall.domain.usecase.orders.InsertOrdersUseCase
import com.judahben149.fourthwall.utils.log
import com.judahben149.fourthwall.utils.text.formatAddSpace
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
import tbdex.sdk.protocol.models.Order
import tbdex.sdk.protocol.models.OrderStatus
import tbdex.sdk.protocol.models.Quote
import tbdex.sdk.protocol.models.Rfq
import web5.sdk.dids.did.BearerDid
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val insertOrdersUseCase: InsertOrdersUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(QuoteState())
    val state: StateFlow<QuoteState> = _state

    fun updateAmount(amount: String) {
        _state.update { it.copy(payInAmount = amount) }
    }

    fun updateSelectedOffering(offering: Offering) {

        val fwOffering = offering.toFwOffering()

        _state.update {
            it.copy(
                tbDexOffering = offering,
                fwOffering = fwOffering,
                exchangeProgress = ExchangeProgress.YetToRequestQuote
            )
        }

    }

    fun updatePayKindNameForFieldsNotRequired(kindName: String) {
        _state.update {
            it.copy(
                payInKind = kindName,
            )
        }
    }

    fun updateRequestedDetailsFields(
        isPayIn: Boolean,
        kindName: String,
        details: Map<String, String>
    ) {
        if (isPayIn) {
            _state.update {
                it.copy(
                    payInKind = kindName,
                    payInRfqRequestFields = details
                )
            }
        } else {
            _state.update {
                it.copy(
                    payOutKind = kindName,
                    payOutRfqRequestFields = details
                )
            }
        }

        state.value.fwOffering?.let { offering ->
            val payOutFields = state.value.payOutRfqRequestFields
            val payInFields = state.value.payInRfqRequestFields
            val payInMethods = offering.payInMethods

            if (payOutFields.isNotEmpty()) {
                // Check if pay-in details are required and if they are provided
                val arePayInDetailsRequired = payInMethods.all { it.paymentFields.isNotEmpty() }
                val arePayInDetailsProvided = payInFields.isNotEmpty()

                if (!arePayInDetailsRequired || (arePayInDetailsRequired && arePayInDetailsProvided)) {
                    _state.update {
                        it.copy(
                            exchangeProgress = ExchangeProgress.IsReadyToRequestQuote
                        )
                    }
                }
            }
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
                        beginPollingForQuoteResponse(rfq, bearerDid)
                    } catch (ex: Exception) {
                        _state.update {
                            it.copy(
                                exchangeProgress = ExchangeProgress.ErrorRequestingQuote(
                                    ex.message.toString()
                                )
                            )
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

        quote?.let { receivedQuote ->
            receivedQuote.toString().log("QUOTE HERE ---->")

            _state.update {
                it.copy(
                    tbDexQuote = receivedQuote,
                    fee = receivedQuote.data.payout.fee?.toDoubleOrNull() ?: 0.0,
                    exchangeProgress = ExchangeProgress.HasGottenQuoteResponse
                )
            }
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
            amount = state.value.payInAmount ?: ""
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

    fun processCloseRequest() {

    }

    fun processOrderRequest() {
        _state.update { state ->
            state.copy(exchangeProgress = ExchangeProgress.IsProcessingOrderRequest)
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                state.value.tbDexQuote?.let {
                    val order = createOrderMessage(it)

                    sessionManager.getBearerDid()?.let { bearerDid -> order.sign(bearerDid) }
                    TbdexHttpClient.submitOrder(order)
                    beginPollingForOrderResponse(order)
                }

            } catch (ex: Exception) {
                _state.update {
                    it.copy(
                        exchangeProgress = ExchangeProgress.HasGottenQuoteResponse
                    )
                }
            }
        }
    }

    private fun createOrderMessage(quote: Quote): Order {

        return Order.create(
            from = sessionManager.getDid()!!,
            to = quote.metadata.from,
            exchangeId = quote.metadata.exchangeId
        )
    }

    private fun beginPollingForOrderResponse(order: Order) {
        var orderStatusUpdate: String? = ""
        var close: Close? = null

        while (close == null) {
            val exchange = TbdexHttpClient.getExchange(
                pfiDid = order.metadata.to,
                requesterDid = sessionManager.getBearerDid()!!,
                exchangeId = order.metadata.exchangeId
            )

            for (message in exchange) {
                when (message) {
                    is OrderStatus -> {
                        // a status update to display to your customer
                        orderStatusUpdate = message.data.orderStatus
                        orderStatusUpdate.log("ORDER STATUS HERE ----> ")

                        _state.update {
                            it.copy(
                                exchangeProgress = ExchangeProgress.HasGottenNewOrderStatusMessage(
                                    message.data.orderStatus
                                )
                            )
                        }
                    }

                    is Close -> {
                        // final message of exchange has been written
                        close = message
                        close.data.reason?.log("ORDER FINAL MESSAGE HERE ----> ")

                        saveOrderToLocalDatabase(order, FwOrderStatus.SUCCESSFUL)

                        if (close.data.success == true) {
                            _state.update {
                                it.copy(
                                    exchangeProgress = ExchangeProgress.HasGottenSuccessfulOrderResponse
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    exchangeProgress = ExchangeProgress.ErrorProcessingOrderMessage(
                                        close.data.reason.toString()
                                    )
                                )
                            }
                        }
                    }

                    else -> {
                        _state.update {
                            it.copy(
                                exchangeProgress = ExchangeProgress.ErrorProcessingOrderMessage(
                                    "Order was not processed"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun saveOrderToLocalDatabase(order: Order, fwOrderStatus: FwOrderStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            val walletAddress =
                if (state.value.payOutKind.toFwPaymentMethod() == PaymentMethods.WALLET_ADDRESS) {
                    try {
                        state.value.payOutRfqRequestFields.getValue("address") as String
                    } catch (ex: Exception) {
                        ""
                    }
                } else {
                    ""
                }

            val recipientAccount =
                if (state.value.payOutKind.toFwPaymentMethod() == PaymentMethods.BANK_TRANSFER) {
                    try {
                        state.value.payOutRfqRequestFields.getValue("accountNumber") as String
                    } catch (ex: Exception) {
                        ""
                    }
                } else {
                    ""
                }


            val orderEntity = order.toFwOrderEntity(
                quote = state.value.tbDexQuote!!,
                payoutMethod = state.value.payOutKind.formatAddSpace(),
                fwOrderStatus = fwOrderStatus,
                orderType = OrderType.SENT,
                walletAddress = walletAddress,
                recipientAccount = recipientAccount,
                fee = state.value.fee
            )

            insertOrdersUseCase(orderEntity)
        }
    }

    fun canSafelyNavigateAway() {
        state.value.run {
            val orderResult = FwOrderResult(
                payInAmount = tbDexQuote!!.data.payin.amount,
                payOutAmount = tbDexQuote.data.payout.amount + tbDexQuote.data.payout.fee + fee,
                payInCurrency = tbDexQuote.data.payin.currencyCode,
                payOutCurrency = tbDexQuote.data.payout.currencyCode,
                pfiDid = tbDexQuote.metadata.from
            )


            _state.update {
                it.copy(exchangeProgress = ExchangeProgress.CanSafeNavigateAway(orderResult))
            }
        }
    }
}

data class QuoteState(
    val payInAmount: String? = null,
    val fwOffering: FWOffering? = null,
    val tbDexOffering: Offering? = null,
    val exchangeProgress: ExchangeProgress = ExchangeProgress.JustStarted,
    val payInKind: String = "",
    val payOutKind: String = "",
    val fee: Double = 0.0,
    val payInRfqRequestFields: Map<String, Any> = emptyMap(),
    val payOutRfqRequestFields: Map<String, Any> = emptyMap(),
    val tbDexQuote: Quote? = null,
    val tbDexOrderStatus: OrderStatus? = null
)

sealed class ExchangeProgress {
    data object JustStarted : ExchangeProgress()
    data object YetToRequestQuote : ExchangeProgress()
    data object IsReadyToRequestQuote : ExchangeProgress()
    data object HasRequestedQuote : ExchangeProgress()
    data object IsPollingForQuoteResponse : ExchangeProgress()
    data object HasGottenQuoteResponse : ExchangeProgress()
    data class ErrorRequestingQuote(val message: String) : ExchangeProgress()
    data object IsProcessingOrderRequest : ExchangeProgress()
    data object HasSentCloseMessage : ExchangeProgress()
    data class HasGottenNewOrderStatusMessage(val message: String) : ExchangeProgress()
    data object HasGottenSuccessfulOrderResponse : ExchangeProgress()
    data object HasGottenCloseResponse : ExchangeProgress()
    data class ErrorProcessingOrderMessage(val message: String) : ExchangeProgress()
    data class ErrorProcessingCloseMessage(val message: String) : ExchangeProgress()
    data class CanSafeNavigateAway(val orderResult: FwOrderResult) : ExchangeProgress()
}