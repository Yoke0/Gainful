package com.yoke.gainful.feature.account.avatar

sealed interface AvatarIntent {
    data class SelectPreset(val emoji: String) : AvatarIntent

    data object Confirm : AvatarIntent

    data class SelectImage(val imageBytes: ByteArray, val fileName: String) : AvatarIntent

    data object ClearError : AvatarIntent
}

enum class AvatarError {
    IMAGE_PROCESS,
    SAVE,
    UPLOAD,
}
