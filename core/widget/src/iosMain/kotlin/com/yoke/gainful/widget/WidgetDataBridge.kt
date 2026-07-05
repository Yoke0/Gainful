package com.yoke.gainful.widget

import com.yoke.gainful.widget.model.PnlWidgetData
import platform.Foundation.NSUserDefaults
import kotlin.time.Clock

object WidgetDataBridge {
    private const val APP_GROUP = "group.com.yoke.gainful"
    private const val KEY_GAIN_TEXT = "widget_gain_text"
    private const val KEY_PCT_TEXT = "widget_pct_text"
    private const val KEY_IS_POSITIVE = "widget_is_positive"
    private const val KEY_HAS_DATA = "widget_has_data"
    private const val KEY_TITLE = "widget_title"
    private const val KEY_NO_DATA = "widget_no_data"
    private const val KEY_LAST_UPDATE = "widget_last_update"

    private fun getDefaults(): NSUserDefaults =
        NSUserDefaults(suiteName = APP_GROUP) ?: NSUserDefaults.standardUserDefaults

    fun save(data: PnlWidgetData) {
        val d = getDefaults()
        d.setObject(data.dailyGainText, forKey = KEY_GAIN_TEXT)
        d.setObject(data.dailyGainPercentText, forKey = KEY_PCT_TEXT)
        d.setBool(data.isPositive, forKey = KEY_IS_POSITIVE)
        d.setBool(data.hasData, forKey = KEY_HAS_DATA)
        d.setObject(data.title, forKey = KEY_TITLE)
        d.setObject(data.noDataText, forKey = KEY_NO_DATA)
        d.setDouble(Clock.System.now().toEpochMilliseconds().toDouble(), forKey = KEY_LAST_UPDATE)
    }
}
