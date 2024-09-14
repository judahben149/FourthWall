package com.judahben149.fourthwall.utils

object Constants {
    // Values
    const val BASE_USER_ID = 100

    // DI Module provider names
    const val FOURTH_WALL_SHARED_PREF = "FOURTH_WALL_SHARED_PREF"
    const val SHARED_PREFERENCES = "SHARED_PREFERENCES"
    const val ENCRYPTED_SHARED_PREFERENCES = "ENCRYPTED_SHARED_PREFERENCES"
    const val FILE_ENCRYPTED_SHARED_PREFERENCES = "FILE_ENCRYPTED_SHARED_PREFERENCES"

    // Database
    const val DATABASE_NAME = "FOURTH_WALL_ROOM_DATABASE"
    const val USER_ID = "USER_ID"

    //Pref Keys
    const val HAS_COMPLETED_ONBOARDING = "HAS_COMPLETED_ONBOARDING"
    const val HAS_STARTED_BUT_NOT_COMPLETED_ONBOARDING = "HAS_STARTED_BUT_NOT_COMPLETED_ONBOARDING"
    const val KCC_VC_JWT = "KCC_VC_JWT"
    const val USER_DID = "USER_DID"

    // Network
    const val VC_BASE_URL = "https://mock-idv.tbddev.org"
    const val KCC_ENDPOINT = "/kcc"

    // Run time constants
    var currencyAccountId = -1

    lateinit var pfiData: Map<String, String> //PFI DID <<<>>> PFI Name
}