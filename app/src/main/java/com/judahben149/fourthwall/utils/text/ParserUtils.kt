package com.judahben149.fourthwall.utils.text

import android.content.Context
import com.fasterxml.jackson.databind.JsonNode
import com.google.gson.JsonParser
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.judahben149.fourthwall.domain.models.PaymentField
import com.judahben149.fourthwall.domain.models.PfiData
import com.judahben149.fourthwall.domain.models.PfiDataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import javax.inject.Inject


class FourthWallParser @Inject constructor(private val context: Context) {

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


fun extractPaymentFields(jsonNode: JsonNode): List<PaymentField> {
    val requiredFields = jsonNode["required"]?.map { it.asText() }?.toSet() ?: emptySet()
    val propertiesNode = jsonNode["properties"]

    val paymentFields = mutableListOf<PaymentField>()

    propertiesNode?.fieldNames()?.forEach { fieldName ->
        val isRequired = requiredFields.contains(fieldName)
        paymentFields.add(PaymentField(fieldName, isRequired))
    }

    return paymentFields
}

fun camelCaseToWords(input: String): String {
    return input.replace(Regex("([a-z])([A-Z])"), "$1 $2")
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}fun String.formatKind(): String {
    val sanitizedString = this.trim().lowercase()

    return when {
        sanitizedString.contains("transfer") -> "Bank Transfer"
        else -> this
    }
}