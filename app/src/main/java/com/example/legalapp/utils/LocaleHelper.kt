package com.example.legalapp.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleHelper {
    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)
        
        return context.createConfigurationContext(config)
    }

    fun getLanguage(context: Context): String {
        return context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
            .getString("language", "en") ?: "en"
    }
} 