package com.judahben149.fourthwall.utils

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.judahben149.fourthwall.domain.models.PfiData
import com.judahben149.fourthwall.domain.models.PfiDataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
            Log.d("TAG", "parseJsonFromAssets: ${e.message}")
            emptyList()
        }
    }
}