package com.judahben149.fourthwall.utils

import java.util.Random
import kotlin.math.abs

fun generateRandomLongId(): Long {
    val currentTimeMillis = System.currentTimeMillis()
    val randomValue = Random().nextLong()
    val combinedValue = currentTimeMillis + randomValue
    return abs(combinedValue)
}