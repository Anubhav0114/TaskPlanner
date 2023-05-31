package com.flaxstudio.taskplanner

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    private const val THEME_PREFS = "theme_prefs"
    private const val THEME_MODE_KEY = "theme_mode"

    enum class ThemeMode(val themeValue: Int) {
        BLUE(0), RED(1), PURPLE(2), ORANGE(3), GREEN(4)
    }

    fun applyTheme(context: Context, themeMode: ThemeMode) {
        val sharedPrefs = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPrefs.edit()

        editor.putInt(THEME_MODE_KEY, themeMode.themeValue)
        editor.apply()

        when (themeMode) {
            ThemeMode.BLUE -> context.setTheme(R.style.Theme_blue)
            ThemeMode.RED -> context.setTheme(R.style.Theme_red)
            ThemeMode.PURPLE -> context.setTheme(R.style.Theme_purple)
            ThemeMode.ORANGE -> context.setTheme(R.style.Theme_orange)
            ThemeMode.GREEN -> context.setTheme(R.style.Theme_green)
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun getSavedThemeMode(context: Context): ThemeMode {
        val sharedPrefs = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
        val savedThemeValue = sharedPrefs.getInt(THEME_MODE_KEY, ThemeMode.BLUE.themeValue)
        return ThemeMode.values().firstOrNull { it.themeValue == savedThemeValue } ?: ThemeMode.BLUE
    }
}
