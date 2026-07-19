package com.yoke.gainful.designsystem.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun Modifier.platformNavigationBarsPadding(): Modifier =
    this.navigationBarsPadding().padding(bottom = 12.dp)
