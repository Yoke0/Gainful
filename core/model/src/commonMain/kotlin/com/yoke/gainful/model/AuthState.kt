package com.yoke.gainful.model

data class AuthState(
    val isLoggedIn: Boolean = false,
    val token: String? = null,
    val userId: String? = null,
    val username: String? = null,
)
