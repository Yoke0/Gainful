package com.yoke.gainful.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yoke.gainful.model.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserDataSource(
    private val dataStore: DataStore<Preferences>,
) {
    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
        val AVATAR_EMOJI = stringPreferencesKey("avatar_emoji")
        val LAST_TRANSACTION_SYNC_TIME = longPreferencesKey("last_transaction_sync_time")
    }

    val userState: Flow<UserState> =
        dataStore.data.map { prefs ->
            UserState(
                isLoggedIn = prefs[Keys.USER_ID] != null,
                userId = prefs[Keys.USER_ID],
                username = prefs[Keys.USERNAME],
            )
        }

    val avatarEmoji: Flow<String?> =
        dataStore.data.map { prefs ->
            prefs[Keys.AVATAR_EMOJI]
        }

    suspend fun saveUser(
        userId: String,
        username: String,
    ) {
        dataStore.edit {
            it[Keys.USER_ID] = userId
            it[Keys.USERNAME] = username
        }
    }

    suspend fun clearUser() {
        dataStore.edit {
            it.remove(Keys.USER_ID)
            it.remove(Keys.USERNAME)
        }
    }

    suspend fun setAvatarEmoji(emoji: String) {
        dataStore.edit {
            it[Keys.AVATAR_EMOJI] = emoji
        }
    }

    val lastTransactionSyncTime: Flow<Long> =
        dataStore.data.map { prefs ->
            prefs[Keys.LAST_TRANSACTION_SYNC_TIME] ?: 0L
        }

    suspend fun saveLastTransactionSyncTime(time: Long) {
        dataStore.edit {
            it[Keys.LAST_TRANSACTION_SYNC_TIME] = time
        }
    }

    suspend fun clearLastTransactionSyncTime() {
        dataStore.edit {
            it.remove(Keys.LAST_TRANSACTION_SYNC_TIME)
        }
    }
}
