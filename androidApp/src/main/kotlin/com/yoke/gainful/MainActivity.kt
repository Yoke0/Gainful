package com.yoke.gainful

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yoke.gainful.di.initKoin
import com.yoke.gainful.widget.WidgetContextHolder
import com.yoke.gainful.widget.WidgetRefreshWorker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        initKoin(applicationContext)
        WidgetContextHolder.init(applicationContext)
        WidgetRefreshWorker.enqueue(applicationContext)

        setContent {
            App()
        }
    }
}
