package com.yoke.gainful.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Border
import com.yoke.gainful.ui.theme.GainRed
import com.yoke.gainful.ui.theme.GainfulTheme
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.GoldDim
import com.yoke.gainful.ui.theme.Surface
import com.yoke.gainful.ui.theme.TextSecondary

@Composable
fun NavButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(Surface)
            .border(1.dp, Border, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
        )
    }
}

@Composable
fun PrimaryButton(
    label: String,
    modifier: Modifier = Modifier,
    color: Color = Gold,
    fontSize: TextUnit = 15.sp,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(50))
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = fontSize,
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
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(50))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(50))
            .clickable(onClick = onClick),
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
    icon: String,
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
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = icon, fontSize = 18.sp)
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
    icon: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .width(44.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = icon,
            fontSize = 20.sp,
            color = TextSecondary,
        )
    }
}

@Preview
@Composable
fun ButtonPreview() {
    var selectedType by remember { mutableStateOf(0) }

    GainfulTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = "NavButton", fontSize = 13.sp, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NavButton("‹") {}
                NavButton("›") {}
            }

            Text(text = "PrimaryButton / SecondaryButton", fontSize = 13.sp, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton(label = "确认", modifier = Modifier.weight(1f), onClick = {})
                SecondaryButton(label = "取消", modifier = Modifier.weight(1f), onClick = {})
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton(label = "危险操作", modifier = Modifier.weight(1f), color = GainRed, onClick = {})
            }

            Text(text = "SelectChip", fontSize = 13.sp, color = TextSecondary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SelectChip(
                    label = "买入", icon = "📈",
                    isSelected = selectedType == 0,
                    activeColor = Gold, activeBackground = GoldDim,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedType = 0 },
                )
                SelectChip(
                    label = "卖出", icon = "📉",
                    isSelected = selectedType == 1,
                    activeColor = GainRed, activeBackground = GainRed.copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f),
                    onClick = { selectedType = 1 },
                )
                SelectChip(
                    label = "分红", icon = "💵",
                    isSelected = selectedType == 2,
                    activeColor = Gold, activeBackground = GoldDim,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedType = 2 },
                )
            }

            Text(text = "SquareIconButton", fontSize = 13.sp, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SquareIconButton(icon = "🔍") {}
                SquareIconButton(icon = "✕") {}
            }
        }
    }
}
