package com.yoke.gainful.designsystem.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun Modifier.platformNavigationBarsPadding(): Modifier =
    this.navigationBarsPadding()
