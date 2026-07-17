package com.yoke.gainful.ksafe

import eu.anifantakis.lib.ksafe.KSafe

class SecureTokenStorage(
    private val ksafe: KSafe,
) {
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    val accessToken: String?
        get() = ksafe.getDirect(KEY_ACCESS_TOKEN, null as String?)

    val refreshToken: String?
        get() = ksafe.getDirect(KEY_REFRESH_TOKEN, null as String?)

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ) {
        ksafe.put(KEY_ACCESS_TOKEN, accessToken)
        ksafe.put(KEY_REFRESH_TOKEN, refreshToken)
    }

    suspend fun clearTokens() {
        ksafe.delete(KEY_ACCESS_TOKEN)
        ksafe.delete(KEY_REFRESH_TOKEN)
    }
}
