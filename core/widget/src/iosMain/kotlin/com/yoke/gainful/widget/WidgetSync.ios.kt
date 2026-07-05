package com.yoke.gainful.widget

import com.yoke.gainful.widget.model.PnlWidgetData

actual suspend fun syncWidgetData(data: PnlWidgetData) {
    WidgetDataBridge.save(data)
}
