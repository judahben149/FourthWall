package com.judahben149.fourthwall.domain.models

data class UserAccount(
    val userId: Int = 0,
    val userName: String = "",
    val userCountryCode: String = "US"
)
