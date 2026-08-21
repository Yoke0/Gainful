package com.yoke.gainful.data.repository

import com.yoke.gainful.model.UserProfile
import com.yoke.gainful.model.UserState
import com.yoke.gainful.network.exception.RefreshProfileResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val userState: Flow<UserState>
    val userProfile: StateFlow<UserProfile?>
    val avatarEmoji: Flow<String?>

    /**
     * Emits the username when the session can no longer be renewed (refresh token expired/revoked),
     * so the UI can redirect to the login screen. Re-emits only after a successful re-login.
     */
    val sessionExpired: SharedFlow<String?>

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

    /** Notifies the app that the session cannot be renewed (e.g. detected by background sync). */
    suspend fun notifySessionExpired()

    suspend fun setAvatarEmoji(emoji: String)

    suspend fun uploadAvatar(imageBytes: ByteArray, fileName: String): Result<Unit>
}
