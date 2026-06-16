package com.yoke.gainful.feature.holdings

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
fun HoldingsScreen(
    viewModel: HoldingsViewModel,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Holdings") },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text("Your Holdings")
        }
    }
}
