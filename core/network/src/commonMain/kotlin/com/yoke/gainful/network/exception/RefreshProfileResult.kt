package com.yoke.gainful.network.exception

sealed class RefreshProfileResult {
    data object Success : RefreshProfileResult()

    data object Unauthorized : RefreshProfileResult()

    data class Error(val cause: Throwable) : RefreshProfileResult()
}
