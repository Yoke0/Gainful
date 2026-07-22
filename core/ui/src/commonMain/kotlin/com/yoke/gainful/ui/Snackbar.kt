package com.yoke.gainful.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.Card
import com.yoke.gainful.designsystem.theme.GainRed
import com.yoke.gainful.designsystem.theme.TextPrimary
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

sealed interface SnackbarEvent {
    val duration: SnackbarDuration

    data class Error(
        override val duration: SnackbarDuration = SnackbarDuration.Short,
    ) : SnackbarEvent

    data class Success(
        override val duration: SnackbarDuration = SnackbarDuration.Short,
    ) : SnackbarEvent
}

class EventChannel<T> {
    private val channel = Channel<T>(Channel.BUFFERED)
    val events = channel.receiveAsFlow()

    suspend fun send(event: T) {
        channel.send(event)
    }
}

val LocalSnackbarHostState =
    compositionLocalOf<SnackbarHostState> {
        error("No SnackbarHostState provided. Wrap content with CompositionLocalProvider.")
    }

@Composable
fun GainfulSnackbar(snackbarData: SnackbarData) {
    Snackbar(
        snackbarData = snackbarData,
        modifier =
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Card)
                .border(1.dp, Border, RoundedCornerShape(10.dp))
                .padding(16.dp),
        shape = RoundedCornerShape(10.dp),
        containerColor = Card,
        contentColor = TextPrimary,
        actionColor = GainRed,
    )
}
