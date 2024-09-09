package com.judahben149.fourthwall.domain.models

import com.judahben149.fourthwall.domain.models.enums.OrderStatus
import com.judahben149.fourthwall.domain.models.enums.OrderType
import com.judahben149.fourthwall.domain.models.enums.PaymentMethods

data class Order(
    val orderId: Int = 0,
    val payInAmount: Double = 0.0,
    val payOutAmount: Double = 0.0,
    val payInCurrency: String,
    val payOutCurrency: String,
    val payOutMethod: PaymentMethods,
    val orderTime: Long,
    val orderType: OrderType,
    val orderStatus: OrderStatus,
    val pfiName: String,
    val receiverName: String = "",
    val walletAddress: String = ""
)
