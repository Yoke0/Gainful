package com.yoke.gainful.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text("App Settings")
        }
    }
}
