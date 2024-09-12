package com.judahben149.fourthwall.domain.mappers

import com.judahben149.fourthwall.data.local.entities.OrderEntity
import com.judahben149.fourthwall.domain.models.FwOrder
import com.judahben149.fourthwall.domain.models.enums.OrderStatus
import com.judahben149.fourthwall.domain.models.enums.OrderType
import com.judahben149.fourthwall.domain.models.enums.PaymentMethods

fun FwOrder.toOrderEntity(): OrderEntity {
    return OrderEntity(
        orderId = orderId,
        payInAmount = payInAmount,
        payOutAmount = payOutAmount,
        payInCurrency = payInCurrency,
        payOutCurrency = payOutCurrency,
        payOutMethod = payOutMethod.ordinal,
        orderTime = orderTime,
        orderType = orderType.ordinal,
        orderStatus = orderStatus.ordinal,
        pfiName = pfiName,
        receiverName = receiverName,
        walletAddress = walletAddress,
    )
}

fun OrderEntity.toOrder(): FwOrder {
    return FwOrder(
        orderId = orderId,
        payInAmount = payInAmount,
        payOutAmount = payOutAmount,
        payInCurrency = payInCurrency,
        payOutCurrency = payOutCurrency,
        payOutMethod = PaymentMethods.entries.getOrNull(payOutMethod)!!,
        orderTime = orderTime,
        orderType = OrderType.entries.getOrNull(orderType)!!,
        orderStatus = OrderStatus.entries.getOrNull(orderStatus)!!,
        pfiName = pfiName,
        receiverName = receiverName,
        walletAddress = walletAddress,

    )
}