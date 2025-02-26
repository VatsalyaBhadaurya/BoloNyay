package com.example.legalapp

import android.app.Application
import android.content.Context
import com.example.legalapp.utils.LocaleHelper

class LegalApp : Application() {
    override fun attachBaseContext(base: Context) {
        val language = LocaleHelper.getLanguage(base)
        super.attachBaseContext(LocaleHelper.setLocale(base, language))
    }
} 