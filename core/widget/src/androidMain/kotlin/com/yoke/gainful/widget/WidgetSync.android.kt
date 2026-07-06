package com.yoke.gainful.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import com.yoke.gainful.widget.model.PnlWidgetData

actual suspend fun syncWidgetData(data: PnlWidgetData) {
    if (!WidgetContextHolder.isInitialized()) return

    val prefs = WidgetContextHolder.getPrefs()
    prefs.edit()
        .putString(WidgetContextHolder.KEY_GAIN_TEXT, data.dailyGainText)
        .putString(WidgetContextHolder.KEY_PCT_TEXT, data.dailyGainPercentText)
        .putBoolean(WidgetContextHolder.KEY_IS_POSITIVE, data.isPositive)
        .putBoolean(WidgetContextHolder.KEY_HAS_DATA, data.hasData)
        .putString(WidgetContextHolder.KEY_TITLE, data.title)
        .putString(WidgetContextHolder.KEY_NO_DATA, data.noDataText)
        .apply()

    triggerWidgetUpdate()
}

private fun triggerWidgetUpdate() {
    val context = WidgetContextHolder.getContext()
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val componentName = ComponentName(context, "com.yoke.gainful.widget.TodayPnlWidgetReceiver")
    val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
    if (widgetIds.isNotEmpty()) {
        val intent =
            Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
                component = componentName
            }
        context.sendBroadcast(intent)
    }
}
