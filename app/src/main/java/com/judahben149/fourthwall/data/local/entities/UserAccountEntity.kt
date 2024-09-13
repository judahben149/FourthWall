package com.judahben149.fourthwall.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey
    val userId: Int,
    val userName: String,
    val userEmail: String,
    val userEncryptedPassword: String,
    val userCountryCode: String
)