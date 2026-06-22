package com.yoke.gainful.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.yoke.gainful.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesDataSource(
    private val dataStore: DataStore<Preferences>,
) {
    private object Keys {
        val REFRESH_MINUTES = intPreferencesKey("refresh_minutes")
        val OPEN_HOUR = intPreferencesKey("open_hour")
        val OPEN_MINUTE = intPreferencesKey("open_minute")
        val CLOSE_HOUR = intPreferencesKey("close_hour")
        val CLOSE_MINUTE = intPreferencesKey("close_minute")
    }

    val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            refreshMinutes = prefs[Keys.REFRESH_MINUTES] ?: UserPreferences().refreshMinutes,
            openHour = prefs[Keys.OPEN_HOUR] ?: UserPreferences().openHour,
            openMinute = prefs[Keys.OPEN_MINUTE] ?: UserPreferences().openMinute,
            closeHour = prefs[Keys.CLOSE_HOUR] ?: UserPreferences().closeHour,
            closeMinute = prefs[Keys.CLOSE_MINUTE] ?: UserPreferences().closeMinute,
        )
    }

    suspend fun setRefreshMinutes(minutes: Int) {
        dataStore.edit { it[Keys.REFRESH_MINUTES] = minutes }
    }

    suspend fun setOpenTime(hour: Int, minute: Int) {
        dataStore.edit {
            it[Keys.OPEN_HOUR] = hour
            it[Keys.OPEN_MINUTE] = minute
        }
    }

    suspend fun setCloseTime(hour: Int, minute: Int) {
        dataStore.edit {
            it[Keys.CLOSE_HOUR] = hour
            it[Keys.CLOSE_MINUTE] = minute
        }
    }
}
