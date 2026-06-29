package com.yoke.gainful.designsystem.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode

private object NoOpIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode = NoOpNode()

    override fun hashCode(): Int = -1

    override fun equals(other: Any?) = other === this
}

private class NoOpNode : Modifier.Node(), DrawModifierNode {
    override fun ContentDrawScope.draw() = drawContent()
}

private val DarkColorScheme =
    darkColorScheme(
        primary = Gold,
        secondary = NeutralGray,
        error = GainRed,
        background = Background,
        surface = Surface,
        surfaceVariant = Surface2,
        onPrimary = Background,
        onSecondary = TextPrimary,
        onBackground = TextPrimary,
        onSurface = TextPrimary,
        onSurfaceVariant = TextSecondary,
        outline = Border,
    )

@Composable
fun GainfulTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
    ) {
        CompositionLocalProvider(LocalIndication provides NoOpIndication) {
            content()
        }
    }
}
