package ru.shirobokov.reanimation.data

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesRepository(val context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences(SERVICES_FILE_NAME, Context.MODE_PRIVATE)

    var isWarningAgree: Boolean
        get() = preferences.getBoolean(IS_WARNING_AGREE_KEY, false)
        set(value) {
            preferences.edit().putBoolean(IS_WARNING_AGREE_KEY, value).apply()
        }

    companion object {
        private const val SERVICES_FILE_NAME = "services_data"

        private const val IS_WARNING_AGREE_KEY = "warning_agree"
    }
}
