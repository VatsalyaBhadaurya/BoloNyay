package com.example.legalapp.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "AppSession",
        Context.MODE_PRIVATE
    )
    private val editor = sharedPreferences.edit()

    companion object {
        const val KEY_IS_FIRST_TIME = "isFirstTime"
        const val KEY_USER_LANGUAGE = "userLanguage"
        const val KEY_USER_NAME = "userName"
    }

    var isFirstTime: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_FIRST_TIME, true)
        set(value) = editor.putBoolean(KEY_IS_FIRST_TIME, value).apply()

    var userLanguage: String
        get() = sharedPreferences.getString(KEY_USER_LANGUAGE, "en") ?: "en"
        set(value) = editor.putString(KEY_USER_LANGUAGE, value).apply()

    var userName: String
        get() = sharedPreferences.getString(KEY_USER_NAME, "") ?: ""
        set(value) = editor.putString(KEY_USER_NAME, value).apply()
} 