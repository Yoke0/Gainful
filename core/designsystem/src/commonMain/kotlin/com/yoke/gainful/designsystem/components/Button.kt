package com.yoke.gainful.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.GainRed
import com.yoke.gainful.designsystem.theme.GainfulTheme
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.GoldDim
import com.yoke.gainful.designsystem.theme.Surface
import com.yoke.gainful.designsystem.theme.TextSecondary
import gainful.core.designsystem.generated.resources.Res
import gainful.core.designsystem.generated.resources.ic_chevron_left
import gainful.core.designsystem.generated.resources.ic_chevron_right
import gainful.core.designsystem.generated.resources.ic_close
import gainful.core.designsystem.generated.resources.ic_coin_dollar
import gainful.core.designsystem.generated.resources.ic_search
import gainful.core.designsystem.generated.resources.ic_trending_down
import gainful.core.designsystem.generated.resources.ic_trending_up
import org.jetbrains.compose.resources.painterResource

@Composable
fun NavIconButton(
    icon: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick)
                .background(Surface)
                .border(1.dp, Border, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = TextSecondary,
        )
    }
}

@Composable
fun PrimaryButton(
    label: String,
    modifier: Modifier = Modifier,
    color: Color = Gold,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .height(36.dp)
                .clip(RoundedCornerShape(50))
                .background(color.copy(alpha = if (enabled) 1f else 0.4f))
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Background,
        )
    }
}

@Composable
fun SecondaryButton(
    label: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 15.sp,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .height(36.dp)
                .clip(RoundedCornerShape(50))
                .background(Surface)
                .border(1.dp, Border, RoundedCornerShape(50))
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
        )
    }
}

@Composable
fun SelectChip(
    label: String,
    icon: Painter,
    iconTint: Color,
    isSelected: Boolean,
    activeColor: Color,
    activeBackground: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bgColor = if (isSelected) activeBackground else Surface
    val borderColor = if (isSelected) activeColor else Border
    val textColor = if (isSelected) activeColor else TextSecondary

    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(50))
                .border(1.dp, borderColor, RoundedCornerShape(50))
                .background(bgColor)
                .clickable(onClick = onClick)
                .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (isSelected) activeColor else iconTint,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
        )
    }
}

@Composable
fun SquareIconButton(
    icon: Painter,
    iconTint: Color = TextSecondary,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .width(44.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Surface)
                .border(1.dp, Border, RoundedCornerShape(10.dp))
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = iconTint,
        )
    }
}

@Preview
@Composable
private fun ButtonPreview() {
    var selectedType by remember { mutableStateOf(0) }

    GainfulTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = "NavIconButton", fontSize = 13.sp, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NavIconButton(painterResource(Res.drawable.ic_chevron_left)) {}
                NavIconButton(painterResource(Res.drawable.ic_chevron_right)) {}
            }

            Text(text = "PrimaryButton / SecondaryButton", fontSize = 13.sp, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton(label = "Confirm", modifier = Modifier.weight(1f), onClick = {})
                SecondaryButton(label = "Cancel", modifier = Modifier.weight(1f), onClick = {})
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton(label = "Danger", modifier = Modifier.weight(1f), color = GainRed, onClick = {})
                PrimaryButton(label = "Disabled", modifier = Modifier.weight(1f), enabled = false, onClick = {})
            }

            Text(text = "SelectChip", fontSize = 13.sp, color = TextSecondary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SelectChip(
                    label = "Buy",
                    icon = painterResource(Res.drawable.ic_trending_up),
                    iconTint = TextSecondary,
                    isSelected = selectedType == 0,
                    activeColor = Gold,
                    activeBackground = GoldDim,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedType = 0 },
                )
                SelectChip(
                    label = "Sell",
                    icon = painterResource(Res.drawable.ic_trending_down),
                    iconTint = TextSecondary,
                    isSelected = selectedType == 1,
                    activeColor = GainRed,
                    activeBackground = GainRed.copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f),
                    onClick = { selectedType = 1 },
                )
                SelectChip(
                    label = "Dividend",
                    icon = painterResource(Res.drawable.ic_coin_dollar),
                    iconTint = TextSecondary,
                    isSelected = selectedType == 2,
                    activeColor = Gold,
                    activeBackground = GoldDim,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedType = 2 },
                )
            }

            Text(text = "SquareIconButton", fontSize = 13.sp, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SquareIconButton(icon = painterResource(Res.drawable.ic_search)) {}
                SquareIconButton(icon = painterResource(Res.drawable.ic_close)) {}
            }
        }
    }
}
