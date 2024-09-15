package com.judahben149.fourthwall.domain

import android.content.Context
import android.content.SharedPreferences
import com.judahben149.fourthwall.utils.AndroidKeyManager
import com.judahben149.fourthwall.utils.Constants
import com.judahben149.fourthwall.utils.Constants.BASE_USER_ID
import com.judahben149.fourthwall.utils.Constants.IS_BIOMETRICS_ENABLED
import com.judahben149.fourthwall.utils.Constants.KCC_VC_JWT
import com.judahben149.fourthwall.utils.Constants.SHOULD_STORE_VC
import com.judahben149.fourthwall.utils.Constants.USER_DID
import com.judahben149.fourthwall.utils.CredentialUtils
import com.judahben149.fourthwall.utils.preferences.fetchBoolean
import com.judahben149.fourthwall.utils.log
import com.judahben149.fourthwall.utils.preferences.saveBoolean
import com.judahben149.fourthwall.utils.preferences.storeSecret
import web5.sdk.dids.did.BearerDid
import web5.sdk.dids.methods.dht.DidDht
import javax.inject.Inject

class SessionManager @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    private val encryptedSharedPrefs: SharedPreferences,
    private val applicationContext: Context,
    credentialUtils: CredentialUtils
) {

    private lateinit var bearerDid: BearerDid

    fun initializeDid() {
        try {
            val keyManager = AndroidKeyManager(applicationContext)
            bearerDid = DidDht.create(keyManager)

            storeDid(bearerDid.uri)
        } catch (ex: Exception) {
            ex.message?.log()
        }
    }

    fun isUserSignedUp(): Boolean {
        return sharedPrefs.getBoolean(Constants.IS_USER_SIGNED_UP, false)
    }

    fun shouldBeginOnboarding(): Boolean {
        return sharedPrefs.getBoolean(Constants.SHOULD_BEGIN_ONBOARDING, true)
    }

    fun updateShouldBeginOnboarding(shouldBegin: Boolean) {
        sharedPrefs.edit().putBoolean(Constants.SHOULD_BEGIN_ONBOARDING, shouldBegin).apply()
    }

    fun updateHasCompletedOnboarding(hasCompleted: Boolean) {
        sharedPrefs.edit().putBoolean(Constants.IS_USER_SIGNED_UP, hasCompleted).apply()
    }

    fun storeKCC(kcc: String) {
        if (isStoringVerifiableCredentialsEnabled()) {
            encryptedSharedPrefs.storeSecret(KCC_VC_JWT, kcc)
        }
    }

    fun getKCC(default: String): String? {
        return encryptedSharedPrefs.getString(KCC_VC_JWT, default)
    }

    fun getDidUri(): String? {
        return encryptedSharedPrefs.getString(USER_DID, null)
    }

    fun storeDid(did: String) {
        encryptedSharedPrefs.storeSecret(USER_DID, did)
    }

    fun getBearerDid(): BearerDid? {
        return if (::bearerDid.isInitialized) bearerDid else null
    }

    fun getUserId(): Int = BASE_USER_ID

    fun toggleBiometrics(isEnabled: Boolean) {
        sharedPrefs.saveBoolean(IS_BIOMETRICS_ENABLED, isEnabled)
    }

    fun isBiometricsEnabled(): Boolean {
        return sharedPrefs.fetchBoolean(IS_BIOMETRICS_ENABLED)
    }

    fun toggleShouldStoreVerifiableCredentials(isEnabled: Boolean) {
        sharedPrefs.saveBoolean(SHOULD_STORE_VC, isEnabled)
    }

    fun isStoringVerifiableCredentialsEnabled(): Boolean {
        return sharedPrefs.fetchBoolean(SHOULD_STORE_VC)
    }

    fun revokeVCs() {
        encryptedSharedPrefs.edit().remove(KCC_VC_JWT).apply()
    }

    fun revokeDid() {
        encryptedSharedPrefs.edit().remove(USER_DID).apply()
    }
}