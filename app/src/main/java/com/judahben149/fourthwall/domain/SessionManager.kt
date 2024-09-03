package com.judahben149.fourthwall.domain

import android.content.SharedPreferences
import com.judahben149.fourthwall.utils.Constants
import com.judahben149.fourthwall.utils.Constants.KCC_VC_JWT
import com.judahben149.fourthwall.utils.storeSecret
import javax.inject.Inject

class SessionManager @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    private val encryptedSharedPrefs: SharedPreferences,
) {

    fun hasCompletedOnboarding(): Boolean {
        return sharedPrefs.getBoolean(Constants.HAS_COMPLETED_ONBOARDING, false)
    }

    fun updateHasCompletedOnboarding(hasCompleted: Boolean) {
        sharedPrefs.edit().putBoolean(Constants.HAS_COMPLETED_ONBOARDING, hasCompleted).apply()
    }

    fun storeKCC(kcc: String) {
        encryptedSharedPrefs.storeSecret(KCC_VC_JWT, kcc)
    }
}