package com.yoke.gainful.data.repository

import com.yoke.gainful.datastore.UserPreferencesDataSource
import com.yoke.gainful.model.UserPreferences
import kotlinx.coroutines.flow.Flow

class OfflineUserPreferencesRepository(
    private val dataSource: UserPreferencesDataSource,
) : UserPreferencesRepository {

    override val userPreferences: Flow<UserPreferences> = dataSource.userPreferences

    override suspend fun setRefreshMinutes(minutes: Int) {
        dataSource.setRefreshMinutes(minutes)
    }

    override suspend fun setOpenTime(hour: Int, minute: Int) {
        dataSource.setOpenTime(hour, minute)
    }

    override suspend fun setCloseTime(hour: Int, minute: Int) {
        dataSource.setCloseTime(hour, minute)
    }
}
