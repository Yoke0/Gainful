package com.yoke.gainful.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yoke.gainful.designsystem.theme.Background

@Composable
fun GainfulScaffold(
    modifier: Modifier = Modifier.padding(horizontal = 16.dp),
    appTopBar: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        appTopBar()

        content()
    }
}
