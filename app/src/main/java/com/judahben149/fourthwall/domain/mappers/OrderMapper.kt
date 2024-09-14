package com.judahben149.fourthwall.domain.mappers

import com.judahben149.fourthwall.data.local.entities.OrderEntity
import com.judahben149.fourthwall.domain.models.FwOrder
import com.judahben149.fourthwall.domain.models.enums.FwOrderStatus
import com.judahben149.fourthwall.domain.models.enums.OrderType
import com.judahben149.fourthwall.domain.models.enums.PaymentMethods
import com.judahben149.fourthwall.utils.Constants
import com.judahben149.fourthwall.utils.getCurrentTimeInMillis
import tbdex.sdk.protocol.models.Order
import tbdex.sdk.protocol.models.Quote

fun FwOrder.toOrderEntity(): OrderEntity {
    return OrderEntity(
        orderId = orderId,
        orderExchangeId = orderExchangeId,
        payInAmount = payInAmount,
        payOutAmount = payOutAmount,
        payInCurrency = payInCurrency,
        payOutCurrency = payOutCurrency,
        payOutMethod = payOutMethod.ordinal,
        orderTime = orderTime,
        orderType = orderType.ordinal,
        orderStatus = fwOrderStatus.ordinal,
        pfiName = pfiName,
        recipientAccount = receiverName,
        walletAddress = walletAddress,
        payoutFee = payoutFee,
    )
}

fun OrderEntity.toFwOrder(): FwOrder {
    return FwOrder(
        orderId = orderId,
        orderExchangeId = orderExchangeId,
        payInAmount = payInAmount,
        payOutAmount = payOutAmount,
        payInCurrency = payInCurrency,
        payOutCurrency = payOutCurrency,
        payOutMethod = PaymentMethods.entries.getOrNull(payOutMethod)!!,
        orderTime = orderTime,
        orderType = OrderType.entries.getOrNull(orderType)!!,
        fwOrderStatus = FwOrderStatus.entries.getOrNull(orderStatus)!!,
        pfiName = pfiName,
        receiverName = recipientAccount,
        walletAddress = walletAddress,
        payoutFee = payoutFee
    )
}

fun Order.toFwOrderEntity(
    quote: Quote,
    payoutMethod: String,
    fwOrderStatus: FwOrderStatus,
    orderType: OrderType,
    walletAddress: String = "",
    recipientAccount: String = "",
    fee: Double
): OrderEntity {

    return OrderEntity(
        orderId = 0,
        orderExchangeId = this.metadata.exchangeId,
        payInAmount = quote.data.payin.amount.toDoubleOrNull() ?: 0.00,
        payOutAmount = quote.data.payout.amount.toDoubleOrNull() ?: 0.00,
        payInCurrency = quote.data.payin.currencyCode,
        payOutCurrency = quote.data.payout.currencyCode,
        payOutMethod = payoutMethod.toFwPaymentMethod().ordinal,
        orderTime = getCurrentTimeInMillis(),
        orderStatus = fwOrderStatus.ordinal,
        orderType = orderType.ordinal,
        pfiName = Constants.pfiData.getValue(quote.metadata.from),
        recipientAccount = recipientAccount,
        walletAddress = walletAddress,
        payoutFee = fee
    )
}

fun String.toFwPaymentMethod(): PaymentMethods {
    return when {
        this.contains("transfer", true) -> PaymentMethods.BANK_TRANSFER
        this.contains("address", true) -> PaymentMethods.WALLET_ADDRESS
        this.contains("balance", true) -> PaymentMethods.STORED_BALANCE
        else -> PaymentMethods.BANK_TRANSFER
    }
}