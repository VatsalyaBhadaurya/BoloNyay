package com.example.legalapp.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREF_NAME = "BoloNyayPrefs"
        private const val KEY_IS_FIRST_TIME = "isFirstTime"
        private const val KEY_USER_LANGUAGE = "userLanguage"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_NAME = "userName"
    }

    var isFirstTime: Boolean
        get() = prefs.getBoolean(KEY_IS_FIRST_TIME, true)
        set(value) = prefs.edit().putBoolean(KEY_IS_FIRST_TIME, value).apply()

    var userLanguage: String
        get() = prefs.getString(KEY_USER_LANGUAGE, "en") ?: "en"
        set(value) = prefs.edit().putString(KEY_USER_LANGUAGE, value).apply()

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    fun setUserLoggedIn(name: String) {
        isLoggedIn = true
        userName = name
    }

    fun logout() {
        prefs.edit().apply {
            remove(KEY_IS_LOGGED_IN)
            remove(KEY_USER_NAME)
            apply()
        }
    }
} 