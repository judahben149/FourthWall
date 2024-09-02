package com.judahben149.fourthwall.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.judahben149.fourthwall.domain.models.PfiData
import com.judahben149.fourthwall.domain.models.PfiDataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import javax.inject.Inject


class PfiDataParser @Inject constructor(private val context: Context) {

    suspend fun parseJsonFromAssets(fileName: String = "pfi_data"): List<PfiData> = withContext(
        Dispatchers.IO) {
        try {
            val inputStream = context.assets.open(fileName)
            val reader = InputStreamReader(inputStream)

            val gson = Gson()
            val pfiDataResponseType = object : TypeToken<PfiDataResponse>() {}.type

            val pfiDataResponse: PfiDataResponse = gson.fromJson(reader, pfiDataResponseType)
            pfiDataResponse.pfiData
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}