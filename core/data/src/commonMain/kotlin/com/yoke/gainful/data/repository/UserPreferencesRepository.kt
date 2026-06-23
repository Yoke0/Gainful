package com.yoke.gainful.data.repository

import com.yoke.gainful.model.GainLossColorScheme
import com.yoke.gainful.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun setRefreshMinutes(minutes: Int)
    suspend fun setOpenTime(hour: Int, minute: Int)
    suspend fun setCloseTime(hour: Int, minute: Int)
    suspend fun setGainLossColorScheme(scheme: GainLossColorScheme)
}
