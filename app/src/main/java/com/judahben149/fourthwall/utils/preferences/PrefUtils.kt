package com.judahben149.fourthwall.utils.preferences

import android.content.SharedPreferences


fun SharedPreferences.saveToPreferences(key: String, value: String) {
    val editor = this.edit()
    editor.putString(key, value)
    editor.apply()
}

fun SharedPreferences.saveInt(key: String, value: Int) {
    val editor = this.edit()
    editor.putInt(key, value)
    editor.apply()
}

fun SharedPreferences.saveLong(key: String, value: Long) {
    val editor = this.edit()
    editor.putLong(key, value)
    editor.apply()
}

fun SharedPreferences.saveBoolean(key: String, value: Boolean) {
    val editor = this.edit()
    editor.putBoolean(key, value)
    editor.apply()
}

fun SharedPreferences.fetchString(key: String, defaultValue: String): String {
    return this.getString(key, defaultValue) ?: defaultValue
}

fun SharedPreferences.fetchInt(key: String, defaultValue: Int): Int {
    return this.getInt(key, defaultValue)
}

fun SharedPreferences.fetchLong(key: String, defaultValue: Long): Long {
    return this.getLong(key, defaultValue)
}

fun SharedPreferences.fetchBoolean(key: String, defaultValue: Boolean = false): Boolean {
    return this.getBoolean(key, defaultValue)
}

fun SharedPreferences.storeSecret(key: String, value: String) {
    val editor = this.edit()
    editor.putString(key, value)
    editor.apply()
}

fun SharedPreferences.storeSecret(key: String, value: Int) {
    val editor = this.edit()
    editor.putInt(key, value)
    editor.apply()
}

fun SharedPreferences.getSecretString(key: String, defaultValue: String): String {
    return this.getString(key, defaultValue) ?: defaultValue
}

fun SharedPreferences.getSecretInt(key: String, defaultValue: Int): Int {
    return this.getInt(key, defaultValue)
}