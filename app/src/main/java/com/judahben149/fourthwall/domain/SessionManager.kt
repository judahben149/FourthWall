package com.judahben149.fourthwall.domain

import android.content.SharedPreferences
import com.judahben149.fourthwall.utils.Constants
import javax.inject.Inject

class SessionManager @Inject constructor(
    private val sharedPrefs: SharedPreferences
) {

    fun hasCompletedOnboarding(): Boolean {
        return sharedPrefs.getBoolean(Constants.HAS_COMPLETED_ONBOARDING, false)
    }

    fun updateHasCompletedOnboarding(hasCompleted: Boolean) {
        sharedPrefs.edit().putBoolean(Constants.HAS_COMPLETED_ONBOARDING, hasCompleted).apply()
    }
}