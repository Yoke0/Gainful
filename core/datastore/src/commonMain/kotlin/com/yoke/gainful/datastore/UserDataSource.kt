package com.yoke.gainful.datastore

import androidx.datastore.core.DataStore
import com.yoke.gainful.model.UserState
import com.yoke.gainful.proto.GainfulDataProto
import com.yoke.gainful.proto.UserProfileProto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserDataSource(
    private val dataStore: DataStore<GainfulDataProto>,
) {
    val userState: Flow<UserState> =
        dataStore.data.map { data ->
            val profile = data.user_profile ?: UserProfileProto()
            UserState(
                isLoggedIn = profile.id.isNotEmpty(),
                userId = profile.id.ifEmpty { null },
                username = profile.username.ifEmpty { null },
            )
        }

    val avatarEmoji: Flow<String?> =
        dataStore.data.map { data ->
            val profile = data.user_profile ?: UserProfileProto()
            profile.avatar_emoji.ifEmpty { null }
        }

    suspend fun saveUser(
        userId: String,
        username: String,
    ) {
        dataStore.updateData { data ->
            val profile = data.user_profile ?: UserProfileProto()
            data.copy(user_profile = profile.copy(id = userId, username = username))
        }
    }

    suspend fun clearUser() {
        dataStore.updateData { data ->
            data.copy(user_profile = UserProfileProto())
        }
    }

    suspend fun setAvatarEmoji(emoji: String) {
        dataStore.updateData { data ->
            val profile = data.user_profile ?: UserProfileProto()
            data.copy(user_profile = profile.copy(avatar_emoji = emoji))
        }
    }
}
