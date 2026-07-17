package com.yoke.gainful.data.repository

import com.yoke.gainful.model.AppSettings
import com.yoke.gainful.model.GainLossColorScheme
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    val appSettings: Flow<AppSettings>

    suspend fun setRefreshMinutes(minutes: Int)

    suspend fun setOpenTime(hour: Int, minute: Int)

    suspend fun setCloseTime(hour: Int, minute: Int)

    suspend fun setGainLossColorScheme(scheme: GainLossColorScheme)
}
