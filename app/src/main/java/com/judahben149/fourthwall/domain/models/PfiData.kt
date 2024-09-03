package com.judahben149.fourthwall.domain.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PfiData(
    @SerializedName("pfi_name") val pfiName: String,
    @SerializedName("pfi_did") val pfiDid: String
)