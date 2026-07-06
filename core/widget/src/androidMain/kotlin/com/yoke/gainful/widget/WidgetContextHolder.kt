package com.yoke.gainful.widget

import android.content.Context
import android.content.SharedPreferences

object WidgetContextHolder {
    private lateinit var appContext: Context
    private const val PREFS_NAME = "widget_pnl_data"
    const val KEY_GAIN_TEXT = "gain_text"
    const val KEY_PCT_TEXT = "pct_text"
    const val KEY_IS_POSITIVE = "is_positive"
    const val KEY_HAS_DATA = "has_data"
    const val KEY_TITLE = "title"
    const val KEY_NO_DATA = "no_data"

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun isInitialized(): Boolean = ::appContext.isInitialized

    fun getPrefs(): SharedPreferences =
        appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getContext(): Context = appContext
}
