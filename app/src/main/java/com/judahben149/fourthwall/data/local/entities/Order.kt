package com.judahben149.fourthwall.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val orderId: Int,
    val amount: Long,
    val payInCurrency: String,
    val payOutCurrency: String,
    val payOutMethod: Int,
    val orderTime: Long,
    val orderStatus: Int,
    val pfiName: String
)

