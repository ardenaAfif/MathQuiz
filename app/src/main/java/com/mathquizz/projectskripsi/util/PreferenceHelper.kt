package com.mathquizz.projectskripsi.util

import android.content.Context

object PreferenceHelper {
    private const val PREF_NAME = "ProjectSkripsiPref"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"

    fun isLoggedIn(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            apply()
        }
    }
}