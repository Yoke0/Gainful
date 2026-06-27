package com.yoke.gainful.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.ui.theme.Background
import com.yoke.gainful.ui.theme.Card
import com.yoke.gainful.ui.theme.GainfulTheme
import com.yoke.gainful.ui.theme.Gold
import com.yoke.gainful.ui.theme.TextMuted
import com.yoke.gainful.ui.theme.TextPrimary
import com.yoke.gainful.ui.theme.TextSecondary

@Composable
fun GainfulTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (navigationIcon != null) {
            navigationIcon()
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                modifier = Modifier.alignByBaseline(),
            )

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 16.sp,
                    color = TextMuted,
                    modifier = Modifier.alignByBaseline(),
                )
            }
        }

        actions()
    }
}

@Composable
fun BackNavigationIcon(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Card)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "\u2039",
            fontSize = 20.sp,
            color = TextSecondary,
        )
    }
}

@Preview
@Composable
private fun GainfulTopAppBarPreview() {
    GainfulTheme {
        GainfulTopAppBar(
            title = "仪表盘",
            modifier = Modifier.background(Background),
        )
    }
}

@Preview
@Composable
private fun GainfulTopAppBarWithBackPreview() {
    GainfulTheme {
        GainfulTopAppBar(
            title = "股票详情",
            navigationIcon = { BackNavigationIcon(onClick = {}) },
            modifier = Modifier.background(Background),
        )
    }
}

@Preview
@Composable
private fun GainfulTopAppBarWithSubtitlePreview() {
    GainfulTheme {
        GainfulTopAppBar(
            title = "贵州茅台",
            subtitle = "600519",
            navigationIcon = { BackNavigationIcon(onClick = {}) },
            modifier = Modifier.background(Background),
        )
    }
}

@Preview
@Composable
private fun GainfulTopAppBarWithActionsPreview() {
    GainfulTheme {
        GainfulTopAppBar(
            title = "交易记录",
            modifier = Modifier.background(Background),
            actions = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Gold)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "+ 新增",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Background,
                    )
                }
            },
        )
    }
}
