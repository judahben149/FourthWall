package com.judahben149.fourthwall.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val orderId: Int,
    val orderExchangeId: String,
    val payInAmount: Double,
    val payOutAmount: Double,
    val payInCurrency: String,
    val payOutCurrency: String,
    val payOutMethod: Int,
    val orderTime: Long,
    val orderType: Int,
    val orderStatus: Int,
    val pfiName: String,
    val pfiDid: String,
    val recipientAccount: String,
    val walletAddress: String,
    val payoutFee: Double,
)

