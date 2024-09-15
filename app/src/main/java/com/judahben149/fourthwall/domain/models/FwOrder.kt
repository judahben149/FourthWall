package com.judahben149.fourthwall.domain.models

import com.judahben149.fourthwall.domain.models.enums.FwOrderStatus
import com.judahben149.fourthwall.domain.models.enums.OrderType
import com.judahben149.fourthwall.domain.models.enums.PaymentMethods

data class FwOrder(
    val orderId: Int = 0,
    val orderExchangeId: String = "",
    val payInAmount: Double = 0.0,
    val payOutAmount: Double = 0.0,
    val payInCurrency: String,
    val payOutCurrency: String,
    val payOutMethod: PaymentMethods,
    val orderTime: Long,
    val orderType: OrderType,
    val fwOrderStatus: FwOrderStatus,
    val pfiName: String,
    val pfiDid: String,
    val recipientAccount: String = "",
    val walletAddress: String = "",
    val payoutFee: Double = 0.0
)
