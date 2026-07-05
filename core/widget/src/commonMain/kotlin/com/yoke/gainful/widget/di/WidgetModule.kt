package com.yoke.gainful.widget.di

import com.yoke.gainful.widget.domain.GetTodayPnlUseCase
import org.koin.dsl.module

val widgetModule =
    module {
        factory { GetTodayPnlUseCase(get()) }
    }
