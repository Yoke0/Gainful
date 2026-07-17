package com.yoke.gainful.data.repository

import com.yoke.gainful.model.UserProfile
import com.yoke.gainful.model.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val userState: Flow<UserState>
    val userProfile: StateFlow<UserProfile?>
    val avatarEmoji: Flow<String?>

    suspend fun login(
        username: String,
        password: String,
    ): Result<Unit>

    suspend fun register(
        nickname: String,
        email: String,
        password: String,
    ): Result<Unit>

    suspend fun logout()

    suspend fun refreshProfile()

    suspend fun setAvatarEmoji(emoji: String)

    suspend fun uploadAvatar(imageBytes: ByteArray, fileName: String): Result<Unit>
}
