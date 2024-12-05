package com.example.test.data.helper

import android.content.Context
import android.content.SharedPreferences

class ThemePreferences(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setDarkModeEnabled(isEnabled: Boolean) {
        preferences.edit().putBoolean(DARK_MODE_KEY, isEnabled).apply()
    }

    fun isDarkModeEnabled(): Boolean {
        return preferences.getBoolean(DARK_MODE_KEY, false)
    }

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val DARK_MODE_KEY = "dark_mode"
    }
}
