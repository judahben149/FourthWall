package com.judahben149.fourthwall.domain

import android.content.Context
import android.content.SharedPreferences
import com.judahben149.fourthwall.utils.AndroidKeyManager
import com.judahben149.fourthwall.utils.Constants
import com.judahben149.fourthwall.utils.Constants.BASE_USER_ID
import com.judahben149.fourthwall.utils.Constants.KCC_VC_JWT
import com.judahben149.fourthwall.utils.Constants.USER_DID
import com.judahben149.fourthwall.utils.log
import com.judahben149.fourthwall.utils.storeSecret
import web5.sdk.dids.did.BearerDid
import web5.sdk.dids.methods.dht.DidDht
import javax.inject.Inject

class SessionManager @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    private val encryptedSharedPrefs: SharedPreferences,
    applicationContext: Context
) {

    private lateinit var bearerDid: BearerDid

    init {
        try {
            val keyManager = AndroidKeyManager(applicationContext)
            bearerDid = DidDht.create(keyManager)

            storeDid(bearerDid.uri)
        } catch (ex: Exception) {
            ex.message?.log()
        }
    }

    fun hasCompletedOnboarding(): Boolean {
        return sharedPrefs.getBoolean(Constants.HAS_COMPLETED_ONBOARDING, false)
    }

    fun updateHasCompletedOnboarding(hasCompleted: Boolean) {
        sharedPrefs.edit().putBoolean(Constants.HAS_COMPLETED_ONBOARDING, hasCompleted).apply()
    }

    fun storeKCC(kcc: String) {
        encryptedSharedPrefs.storeSecret(KCC_VC_JWT, kcc)
    }

    fun getKCC(default: String): String? {
        return encryptedSharedPrefs.getString(KCC_VC_JWT, default)
    }

    fun getDid(): String? {
        return encryptedSharedPrefs.getString(USER_DID, null)
    }

    fun storeDid(did: String) {
        encryptedSharedPrefs.storeSecret(USER_DID, did)
    }

    fun getBearerDid(): BearerDid? {
        return if (::bearerDid.isInitialized) bearerDid else null
    }

    fun getUserId(): Int = BASE_USER_ID
}