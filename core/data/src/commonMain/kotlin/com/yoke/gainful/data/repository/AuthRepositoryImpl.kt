package com.yoke.gainful.data.repository

import com.yoke.gainful.api.LoginRequest
import com.yoke.gainful.api.RegisterRequest
import com.yoke.gainful.database.dao.SyncQueueDao
import com.yoke.gainful.database.dao.TransactionDao
import com.yoke.gainful.datastore.SyncDataSource
import com.yoke.gainful.datastore.UserDataSource
import com.yoke.gainful.model.UserProfile
import com.yoke.gainful.model.UserState
import com.yoke.gainful.network.exception.RefreshProfileResult
import com.yoke.gainful.network.exception.RefreshTokenExpiredException
import com.yoke.gainful.network.server.AuthenticatedApi
import com.yoke.gainful.network.server.PublicApi
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

internal class AuthRepositoryImpl(
    private val publicApi: PublicApi,
    private val authenticatedApi: AuthenticatedApi,
    private val userDataSource: UserDataSource,
    private val transactionDao: TransactionDao,
    private val syncQueueDao: SyncQueueDao,
    private val syncDataSource: SyncDataSource,
) : AuthRepository {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    override val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    override val userState: Flow<UserState> = userDataSource.userState
    override val avatarEmoji: Flow<String?> = userDataSource.avatarEmoji

    private val _sessionExpired = MutableSharedFlow<String?>(extraBufferCapacity = 1)
    override val sessionExpired: SharedFlow<String?> = _sessionExpired.asSharedFlow()
    private var sessionExpiredNotified = false

    override suspend fun login(
        username: String,
        password: String,
    ): Result<Unit> =
        runCatching {
            val resp = publicApi.login(LoginRequest(username, password))
            // Drop any tokens cached in-memory by the auth client so the next
            // authenticated call reloads the freshly issued tokens from storage.
            authenticatedApi.clearCachedTokens()
            userDataSource.saveUser(resp.userId, resp.username)
            val profileResult = refreshProfile()
            check(profileResult is RefreshProfileResult.Success) { "Failed to load profile" }
        }

    override suspend fun register(
        nickname: String,
        email: String,
        password: String,
    ): Result<Unit> =
        runCatching {
            val resp = publicApi.register(RegisterRequest(username = nickname, email, password))
            authenticatedApi.clearCachedTokens()
            userDataSource.saveUser(resp.userId, resp.username)
            val profileResult = refreshProfile()
            check(profileResult is RefreshProfileResult.Success) { "Failed to load profile" }
        }

    override suspend fun logout() {
        // 1. Revoke the session server-side (bearer credentials attached/refreshed automatically).
        runCatching { authenticatedApi.logout() }
        // 2. Clear the in-memory auth client cache so stale tokens never leak into a new login.
        runCatching { authenticatedApi.clearCachedTokens() }
        // 3. Clear local state.
        runCatching { publicApi.logout() }
        transactionDao.deleteAll()
        syncQueueDao.deleteAll()
        syncDataSource.clearSyncState()
        userDataSource.clearUser()
        _userProfile.value = null
        sessionExpiredNotified = false
    }

    override suspend fun refreshProfile(): RefreshProfileResult {
        val resp =
            try {
                authenticatedApi.getProfile()
            } catch (e: RefreshTokenExpiredException) {
                notifySessionExpired()
                return RefreshProfileResult.Unauthorized
            } catch (e: ResponseException) {
                if (e.response.status == HttpStatusCode.Unauthorized) {
                    notifySessionExpired()
                    return RefreshProfileResult.Unauthorized
                }
                return RefreshProfileResult.Error(e)
            } catch (e: Exception) {
                return RefreshProfileResult.Error(e)
            }
        _userProfile.value =
            UserProfile(
                id = resp.id,
                username = resp.username,
                email = resp.email,
                nickname = resp.nickname,
                avatarUrl = resp.avatarUrl,
            )
        sessionExpiredNotified = false
        return RefreshProfileResult.Success
    }

    override suspend fun notifySessionExpired() {
        notifySessionExpiredInternal()
    }

    private suspend fun notifySessionExpiredInternal() {
        if (sessionExpiredNotified) return
        sessionExpiredNotified = true
        val username = userDataSource.userState.first().username
        _sessionExpired.emit(username)
    }

    override suspend fun setAvatarEmoji(emoji: String) {
        userDataSource.setAvatarEmoji(emoji)
    }

    override suspend fun uploadAvatar(imageBytes: ByteArray, fileName: String): Result<Unit> =
        runCatching {
            val avatarUrl = authenticatedApi.uploadAvatar(imageBytes, fileName)
            _userProfile.value = _userProfile.value?.copy(avatarUrl = avatarUrl)
        }
}
