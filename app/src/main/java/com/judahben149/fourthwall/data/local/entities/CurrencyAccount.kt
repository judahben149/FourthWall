package com.judahben149.fourthwall.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "currency_accounts",
    foreignKeys = [ForeignKey(
        entity = UserAccount::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["userId"])]
)
data class CurrencyAccount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int, // Do not alter the name "userId", it is relationally linked to another entity
    val currencyCode: String,
    val balance: Double,
    val isPrimaryAccount: Int = 0
)