package com.judahben149.fourthwall.utils.text

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.judahben149.fourthwall.domain.models.PfiData
import com.judahben149.fourthwall.domain.models.PfiDataResponse
import com.judahben149.fourthwall.presentation.rfq.RequestQuoteFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tbdex.sdk.protocol.models.PaymentMethod
import java.io.InputStreamReader
import javax.inject.Inject


class PfiDataParser @Inject constructor(private val context: Context) {

    suspend fun parseJsonFromAssets(fileName: String = "pfi_data.json"): List<PfiData> = withContext(
        Dispatchers.IO) {
        try {
            val inputStream = context.assets.open(fileName)
            val reader = InputStreamReader(inputStream)

            val gson = GsonBuilder().setLenient().create()
            val pfiDataResponseType = object : TypeToken<PfiDataResponse>() {}.type

            val pfiDataResponse: PfiDataResponse = gson.fromJson(reader, pfiDataResponseType)
            pfiDataResponse.pfiData
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}


private fun getStatusText(status: Int): String {
    return when (status) {
        0 -> "In-Transit"
        1 -> "Success"
        else -> "Failed"
    }
}

//fun PaymentMethod.parseRequiredPaymentDetails(): RequestQuoteFragment.RequiredPaymentDetails? {
//    val gson = Gson()
//
//    return try {
//        val jsonObject = gson.fromJson(requiredPaymentDetails.toString(), JsonObject::class.java)
//
//        val title = jsonObject.get("title").asString
//        val type = jsonObject.get("type").asString
//        val required = gson.fromJson<List<String>>(jsonObject.get("required"), object : TypeToken<List<String>>() {}.type)
//
//        val propertiesObject = jsonObject.getAsJsonObject("properties")
//        val properties = mutableMapOf<String, RequestQuoteFragment.SchemaField>()
//
//        propertiesObject.keySet().forEach { key ->
//            val fieldObject = propertiesObject.getAsJsonObject(key)
//            properties[key] = RequestQuoteFragment.SchemaField(
//                title = fieldObject.get("title").asString,
//                description = fieldObject.get("description").asString,
//                type = fieldObject.get("type").asString
//            )
//        }
//
//        val schema = RequestQuoteFragment.RequiredPaymentDetailsSchema(
//            title = title,
//            type = type,
//            required = required,
//            properties = properties
//        )
//
//        RequestQuoteFragment.RequiredPaymentDetails(kind, schema)
//    } catch (e: Exception) {
//        e.printStackTrace()
//        null
//    }
//}