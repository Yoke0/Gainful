package com.yoke.gainful.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yoke.gainful.model.AuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthDataSource(
    private val dataStore: DataStore<Preferences>,
) {
    private object Keys {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
        val AVATAR_EMOJI = stringPreferencesKey("avatar_emoji")
    }

    val authState: Flow<AuthState> =
        dataStore.data.map { prefs ->
            val token = prefs[Keys.AUTH_TOKEN]
            AuthState(
                isLoggedIn = token != null,
                token = token,
                userId = prefs[Keys.USER_ID],
                username = prefs[Keys.USERNAME],
            )
        }

    val avatarEmoji: Flow<String?> =
        dataStore.data.map { prefs ->
            prefs[Keys.AVATAR_EMOJI]
        }

    suspend fun saveAuth(
        token: String,
        userId: String,
        username: String,
    ) {
        dataStore.edit {
            it[Keys.AUTH_TOKEN] = token
            it[Keys.USER_ID] = userId
            it[Keys.USERNAME] = username
        }
    }

    suspend fun clearAuth() {
        dataStore.edit {
            it.remove(Keys.AUTH_TOKEN)
            it.remove(Keys.USER_ID)
            it.remove(Keys.USERNAME)
        }
    }

    suspend fun setAvatarEmoji(emoji: String) {
        dataStore.edit {
            it[Keys.AVATAR_EMOJI] = emoji
        }
    }
}
