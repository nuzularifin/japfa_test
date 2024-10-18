package com.dicoding.japfatest.utils

import android.content.Context

class PreferenceHelper(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveLoginStatus(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("is_logged_in", isLoggedIn).apply()
    }

    fun getLoginStatus(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }
}