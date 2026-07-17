package com.yoke.gainful.data.repository

import com.yoke.gainful.api.LoginRequest
import com.yoke.gainful.api.RegisterRequest
import com.yoke.gainful.database.dao.SyncQueueDao
import com.yoke.gainful.database.dao.TransactionDao
import com.yoke.gainful.datastore.UserDataSource
import com.yoke.gainful.model.UserProfile
import com.yoke.gainful.model.UserState
import com.yoke.gainful.network.server.AuthenticatedApi
import com.yoke.gainful.network.server.PublicApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class AuthRepositoryImpl(
    private val publicApi: PublicApi,
    private val authenticatedApi: AuthenticatedApi,
    private val userDataSource: UserDataSource,
    private val transactionDao: TransactionDao,
    private val syncQueueDao: SyncQueueDao,
) : AuthRepository {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    override val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    override val userState: Flow<UserState> = userDataSource.userState
    override val avatarEmoji: Flow<String?> = userDataSource.avatarEmoji

    override suspend fun login(
        username: String,
        password: String,
    ): Result<Unit> =
        runCatching {
            val resp = publicApi.login(LoginRequest(username, password))
            userDataSource.saveUser(resp.userId, resp.username)
            refreshProfile()
        }

    override suspend fun register(
        nickname: String,
        email: String,
        password: String,
    ): Result<Unit> =
        runCatching {
            val resp = publicApi.register(RegisterRequest(username = nickname, email, password))
            userDataSource.saveUser(resp.userId, resp.username)
            refreshProfile()
        }

    override suspend fun logout() {
        runCatching { publicApi.logout() }
        transactionDao.deleteAll()
        syncQueueDao.deleteAll()
        userDataSource.clearUser()
        _userProfile.value = null
    }

    override suspend fun refreshProfile() {
        val resp = authenticatedApi.getProfile()
        _userProfile.value =
            UserProfile(
                id = resp.id,
                username = resp.username,
                email = resp.email,
                nickname = resp.nickname,
                avatarUrl = resp.avatarUrl,
            )
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
