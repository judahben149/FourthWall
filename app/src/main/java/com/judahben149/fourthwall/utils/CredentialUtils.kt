package com.judahben149.fourthwall.utils

import android.content.Context
import com.google.gson.Gson
import com.judahben149.fourthwall.domain.SessionManager
import com.judahben149.fourthwall.utils.preferences.PrefManager
import web5.sdk.dids.did.BearerDid
import web5.sdk.dids.did.PortableDid
import web5.sdk.dids.methods.dht.DidDht
import javax.inject.Inject

class CredentialUtils @Inject constructor() {

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var prefManager: PrefManager

    fun createDid() {

        val keyManager = AndroidKeyManager(appContext)
//        val bearerDid = DidDht.create(keyManager)

        val portableDid = DidDht.create(keyManager).export()
        storePortableDid(portableDid)
    }

    private fun storePortableDid(portableDid: PortableDid) {
        val gson = Gson()
        val serializedPortableDid = gson.toJson(portableDid)

        prefManager.storeDid(serializedPortableDid)
    }

    fun retrieveDid(): BearerDid? {
        return prefManager.getDid()?.let {
            val gson = Gson()
            val deserializedPortableDid = gson.fromJson(it, PortableDid::class.java)

            BearerDid.import(deserializedPortableDid, AndroidKeyManager(appContext))
        }
    }

    fun retrieveDidUri(): String? {
        return prefManager.getDid()?.let {
            val gson = Gson()
            val deserializedPortableDid = gson.fromJson(it, PortableDid::class.java)

            val bearerDid = BearerDid.import(deserializedPortableDid, AndroidKeyManager(appContext))
            bearerDid.uri
        }
    }

//    fun compareDids(): Boolean {
//        val storedBearerDid =
//    }
}