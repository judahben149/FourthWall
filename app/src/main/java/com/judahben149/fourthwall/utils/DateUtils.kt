package com.judahben149.fourthwall.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun convertToCasualFriendlyDate(isoDateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date = inputFormat.parse(isoDateString)

    return if (date != null) {
        val day = SimpleDateFormat("d", Locale.getDefault()).format(date).toInt()
        val suffix = when (day % 10) {
            1 -> if (day == 11) "th" else "st"
            2 -> if (day == 12) "th" else "nd"
            3 -> if (day == 13) "th" else "rd"
            else -> "th"
        }
        SimpleDateFormat("EEE, MMM d'$suffix', yyyy", Locale.getDefault()).format(date)
    } else {
        "Invalid Date"
    }
}


fun OffsetDateTime.toCasualFriendlyDate(): String {
    val day = this.dayOfMonth
    val suffix = when (day % 10) {
        1 -> if (day == 11) "th" else "st"
        2 -> if (day == 12) "th" else "nd"
        3 -> if (day == 13) "th" else "rd"
        else -> "th"
    }

    val formatter = DateTimeFormatter.ofPattern("EEE, MMM d'$suffix', yyyy", Locale.getDefault())

    return this.format(formatter)
}

fun getCurrentTimeInMillis(): Long {
    return System.currentTimeMillis()
}