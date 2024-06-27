package com.uinsuka.uas.targetshooter.settings

import android.app.Application
import android.content.Context

class FontScaleSetting : Application() {
    override fun onCreate() {
        super.onCreate()
        resetFontScale(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }

    companion object {
        fun resetFontScale(context: Context) {
            val configuration = context.resources.configuration
            configuration.fontScale = 1.05f
            val metrics = context.resources.displayMetrics
            @Suppress("DEPRECATION")
            metrics.scaledDensity = configuration.fontScale * metrics.density
            context.createConfigurationContext(configuration)
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(configuration, metrics)
        }

        fun updateBaseContextLocale(context: Context): Context {
            val configuration = context.resources.configuration
            configuration.fontScale = 1.05f
            return context.createConfigurationContext(configuration)
        }
    }
}