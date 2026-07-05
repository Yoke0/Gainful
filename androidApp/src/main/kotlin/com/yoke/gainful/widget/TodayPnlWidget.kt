package com.yoke.gainful.widget

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.yoke.gainful.MainActivity
import com.yoke.gainful.R
import com.yoke.gainful.di.initKoin
import com.yoke.gainful.widget.domain.GetTodayPnlUseCase
import com.yoke.gainful.widget.model.PnlWidgetData
import org.koin.core.context.GlobalContext

private val WidgetBg = ColorProvider(Color(0xFF070B15), Color(0xFF0A0E19))
private val TextGray = ColorProvider(Color(0xFF8A8A9A), Color(0xFF9A9AAA))
private val GainGold = ColorProvider(Color(0xFFFFD700), Color(0xFFFFDF40))
private val LossRed = ColorProvider(Color(0xFFE74C3C), Color(0xFFFF5C4C))
private val GainGreen = ColorProvider(Color(0xFF4ADE80), Color(0xFF5EED90))

class TodayPnlWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        initKoin(context)

        val useCase = GlobalContext.get().get<GetTodayPnlUseCase>()
        val title = context.getString(R.string.widget_today_pnl_title)
        val noData = context.getString(R.string.widget_no_data)
        val data =
            runCatching { useCase.compute(title, noData) }
                .getOrDefault(PnlWidgetData(title = title, noDataText = noData))

        provideContent {
            GlanceTheme {
                PnlWidgetContent(context, data)
            }
        }
    }
}

@Composable
private fun PnlWidgetContent(context: Context, data: PnlWidgetData) {
    val componentName = ComponentName(context, MainActivity::class.java)
    val size = LocalSize.current
    val compact = size.height < 130.dp

    Box(
        modifier =
            GlanceModifier
                .fillMaxSize()
                .background(WidgetBg)
                .cornerRadius(16.dp)
                .padding(if (compact) 10.dp else 14.dp)
                .clickable(actionStartActivity(componentName)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = data.title,
                style =
                    TextStyle(
                        color = TextGray,
                        fontSize = if (compact) 12.sp else 13.sp,
                        fontWeight = FontWeight.Medium,
                    ),
            )
            Spacer(modifier = GlanceModifier.height(if (compact) 4.dp else 6.dp))

            if (!data.hasData) {
                Text(
                    text = data.noDataText,
                    style = TextStyle(color = TextGray, fontSize = if (compact) 14.sp else 15.sp),
                )
            } else {
                val valueColor = if (data.isPositive) GainGold else LossRed
                Text(
                    text = data.dailyGainText,
                    style =
                        TextStyle(
                            color = valueColor,
                            fontSize = if (compact) 22.sp else 28.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                )
                if (!compact) {
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    val pctColor = if (data.isPositive) GainGreen else LossRed
                    Text(
                        text = data.dailyGainPercentText,
                        style = TextStyle(color = pctColor, fontSize = 15.sp, fontWeight = FontWeight.Bold),
                    )
                }
            }
        }
    }
}
