package com.yoke.gainful.model

data class UserProfile(
    val id: String,
    val username: String,
    val email: String,
    val nickname: String? = null,
    val avatarUrl: String? = null,
)
