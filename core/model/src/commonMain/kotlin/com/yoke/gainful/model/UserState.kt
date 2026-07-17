package com.yoke.gainful.model

data class UserState(
    val isLoggedIn: Boolean = false,
    val userId: String? = null,
    val username: String? = null,
)
