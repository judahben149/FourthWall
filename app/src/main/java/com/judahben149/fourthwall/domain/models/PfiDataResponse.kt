package com.judahben149.fourthwall.domain.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PfiDataResponse(
    @SerializedName("pfi_data") val pfiData: List<PfiData>
)