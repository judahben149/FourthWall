package com.judahben149.fourthwall.utils.preferences

import android.content.SharedPreferences
import com.judahben149.fourthwall.utils.Constants
import javax.inject.Inject

class PrefManager @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    private val encryptedSharedPrefs: SharedPreferences,
) {

    fun getDid(): String? {
        return encryptedSharedPrefs.getString(Constants.USER_DID, null)
    }

    fun storeDid(did: String) {
        encryptedSharedPrefs.storeSecret(Constants.USER_DID, did)
    }
}