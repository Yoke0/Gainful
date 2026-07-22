package com.yoke.gainful.data.repository

import com.yoke.gainful.model.UserProfile
import com.yoke.gainful.model.UserState
import com.yoke.gainful.network.exception.RefreshProfileResult
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

    suspend fun refreshProfile(): RefreshProfileResult

    suspend fun setAvatarEmoji(emoji: String)

    suspend fun uploadAvatar(imageBytes: ByteArray, fileName: String): Result<Unit>
}
