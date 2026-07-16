package com.yoke.gainful.data.repository

import com.yoke.gainful.datastore.AuthDataSource
import com.yoke.gainful.model.AuthState
import com.yoke.gainful.model.UserProfile
import com.yoke.gainful.network.GainfulApi
import com.yoke.gainful.network.model.LoginRequest
import com.yoke.gainful.network.model.RegisterRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

internal class AuthRepositoryImpl(
    private val api: GainfulApi,
    private val authDataSource: AuthDataSource,
) : AuthRepository {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    override val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    override val authState: Flow<AuthState> = authDataSource.authState
    override val avatarEmoji: Flow<String?> = authDataSource.avatarEmoji

    override suspend fun login(
        username: String,
        password: String,
    ): Result<Unit> =
        runCatching {
            val resp = api.login(LoginRequest(username, password))
            authDataSource.saveAuth(resp.token, resp.userId, resp.username)
            refreshProfile()
        }

    override suspend fun register(
        nickname: String,
        email: String,
        password: String,
    ): Result<Unit> =
        runCatching {
            val resp = api.register(RegisterRequest(username = nickname, email, password))
            authDataSource.saveAuth(resp.token, resp.userId, resp.username)
            refreshProfile()
        }

    override suspend fun logout() {
        val state = authState.first()
        state.token?.let { token ->
            runCatching { api.revokeSessions(token) }
        }
        authDataSource.clearAuth()
        _userProfile.value = null
    }

    override suspend fun refreshProfile() {
        val state = authState.first()
        state.token?.let { token ->
            val resp = api.getProfile(token)
            _userProfile.value =
                UserProfile(
                    id = resp.id,
                    username = resp.username,
                    email = resp.email,
                    nickname = resp.nickname,
                    avatarUrl = resp.avatarUrl,
                )
        }
    }

    override suspend fun setAvatarEmoji(emoji: String) {
        authDataSource.setAvatarEmoji(emoji)
    }

    override suspend fun uploadAvatar(imageBytes: ByteArray, fileName: String): Result<Unit> =
        runCatching {
            val state = authState.first()
            val token = state.token ?: throw IllegalStateException("Not logged in")
            val resp = api.uploadAvatar(token, imageBytes, fileName)
            _userProfile.value =
                UserProfile(
                    id = resp.id,
                    username = resp.username,
                    email = resp.email,
                    nickname = resp.nickname,
                    avatarUrl = resp.avatarUrl,
                )
        }
}
