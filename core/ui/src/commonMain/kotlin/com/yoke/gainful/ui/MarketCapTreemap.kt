package com.yoke.gainful.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.designsystem.theme.Amber
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.Blue
import com.yoke.gainful.designsystem.theme.Coral
import com.yoke.gainful.designsystem.theme.GainGreen
import com.yoke.gainful.designsystem.theme.GainRed
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.LightBlue
import com.yoke.gainful.designsystem.theme.LightGreen
import com.yoke.gainful.designsystem.theme.Orange
import com.yoke.gainful.designsystem.theme.Pink
import com.yoke.gainful.designsystem.theme.Purple
import com.yoke.gainful.designsystem.theme.Teal
import gainful.core.ui.generated.resources.Res
import gainful.core.ui.generated.resources.treemap_other
import org.jetbrains.compose.resources.stringResource

data class TreemapItem(
    val name: String,
    val percentage: Float,
)

data class TreemapRect(
    val item: TreemapItem,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
)

private val TreemapPalette =
    listOf(
        Blue,
        GainGreen,
        Gold,
        GainRed,
        Purple,
        Teal,
        Orange,
        LightBlue,
        LightGreen,
        Coral,
        Amber,
        Pink,
    )

@Composable
fun MarketCapTreemap(
    items: List<TreemapItem>,
    modifier: Modifier = Modifier,
    otherLabel: String = stringResource(Res.string.treemap_other),
) {
    val sortedItems = remember(items) { items.sortedByDescending { it.percentage } }
    val textMeasurer = rememberTextMeasurer()

    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(RoundedCornerShape(6.dp)),
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        val densityValue = density.density

        val rects =
            remember(sortedItems, widthPx, heightPx, densityValue) {
                val processed = mergeSmallItems(sortedItems, widthPx, heightPx, densityValue, textMeasurer, otherLabel)
                computeTreemapLayout(processed, 0f, 0f, widthPx, heightPx)
            }

        rects.forEachIndexed { index, rect ->
            val xDp = with(density) { rect.x.toDp() }
            val yDp = with(density) { rect.y.toDp() }
            val widthDp = with(density) { rect.width.toDp() }
            val heightDp = with(density) { rect.height.toDp() }
            val color = TreemapPalette[index % TreemapPalette.size]

            TreemapBlock(
                rect = rect,
                color = color,
                modifier =
                    Modifier
                        .offset { IntOffset(xDp.roundToPx(), yDp.roundToPx()) }
                        .size(widthDp, heightDp),
            )
        }
    }
}

@Composable
private fun TreemapBlock(
    rect: TreemapRect,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val item = rect.item
    val density = LocalDensity.current
    val area = rect.width * rect.height
    val densityVal = density.density
    val (nameSp, pctSp) = treemapFontSize(area, densityVal)
    val minNameWidth = nameSp * densityVal * 2f + 8f * densityVal
    val minNameHeight = (nameSp + pctSp + 4f) * densityVal

    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(6.dp))
                .background(color),
        contentAlignment = Alignment.Center,
    ) {
        if (rect.width >= minNameWidth && rect.height >= minNameHeight) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((-2).dp, Alignment.CenterVertically),
                modifier = Modifier.padding(horizontal = 4.dp),
            ) {
                Text(
                    text = item.name,
                    color = Color.White,
                    fontSize = nameSp.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    style =
                        TextStyle(
                            lineHeight = nameSp.sp,
                            lineHeightStyle =
                                LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both,
                                ),
                        ),
                )
                val displayPct = if (item.percentage >= 1f) item.percentage.toInt().toString() else "<1"
                Text(
                    text = "$displayPct%",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = pctSp.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    style =
                        TextStyle(
                            lineHeight = pctSp.sp,
                            lineHeightStyle =
                                LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both,
                                ),
                        ),
                )
            }
        }
    }
}

private fun mergeSmallItems(
    items: List<TreemapItem>,
    containerWidth: Float,
    containerHeight: Float,
    density: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    otherLabel: String,
): List<TreemapItem> {
    if (items.isEmpty()) return emptyList()
    if (items.size == 1) return items

    val sorted = items.sortedByDescending { it.percentage }
    val keepItems = sorted.toMutableList()
    val otherItems = mutableListOf<TreemapItem>()

    // Iteratively move the smallest items to "Other" until the "Other" block can fit text
    // or until we only have one item left.
    var currentResult = keepItems.toList()

    while (keepItems.size > 1) {
        val rects = computeTreemapLayout(currentResult, 0f, 0f, containerWidth, containerHeight)

        // Check if all current items fit. The last one is the potential "Other" or the smallest.
        val lastIdx = currentResult.size - 1
        val lastRect = if (lastIdx < rects.size) rects[lastIdx] else null
        val lastItem = currentResult[lastIdx]

        if (lastRect != null && canFitText(lastItem, lastRect, density, textMeasurer)) {
            // If the smallest item (or current "Other") fits, we are good.
            return currentResult
        }

        // Doesn't fit, move the smallest real item to otherItems and re-calculate "Other"
        val itemToMove = keepItems.removeAt(keepItems.size - 1)
        otherItems.add(itemToMove)

        val otherPercentage = otherItems.sumOf { it.percentage.toDouble() }.toFloat()
        // Ensure "Other" is slightly smaller than the new smallest kept item
        val minKept = keepItems.last().percentage
        val cappedPercentage = minOf(otherPercentage, minKept * 0.95f)

        currentResult = keepItems + TreemapItem(name = otherLabel, percentage = cappedPercentage)
    }

    return currentResult
}

private fun treemapFontSize(area: Float, density: Float): Pair<Float, Float> {
    val d2 = density * density
    return when {
        area < 3000 * d2 -> 9f to 8f
        area < 6000 * d2 -> 10f to 9f
        area < 12000 * d2 -> 12f to 10f
        area < 24000 * d2 -> 14f to 11f
        else -> 16f to 12f
    }
}

private fun canFitText(
    item: TreemapItem,
    rect: TreemapRect,
    density: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
): Boolean {
    val area = rect.width * rect.height
    val (nameSp, pctSp) = treemapFontSize(area, density)
    val minWidth = nameSp * density * 2f + 8f * density
    val minHeight = (nameSp + pctSp + 4f) * density

    if (rect.width < minWidth || rect.height < minHeight) {
        return false
    }

    val padding = 4f * density
    val nameStyle = TextStyle(fontSize = nameSp.sp)
    val textWidth = textMeasurer.measure(item.name, nameStyle).size.width.toFloat()

    return textWidth <= rect.width - padding * 2
}

private fun computeTreemapLayout(
    items: List<TreemapItem>,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
): List<TreemapRect> {
    if (items.isEmpty()) return emptyList()
    if (items.size == 1) {
        return listOf(TreemapRect(items[0], x, y, maxOf(0f, width), maxOf(0f, height)))
    }

    val total = items.sumOf { it.percentage.toDouble() }.toFloat()
    if (total <= 0f) {
        val subWidth = if (width >= height) width / items.size else width
        val subHeight = if (width >= height) height else height / items.size
        return items.mapIndexed { i, item ->
            TreemapRect(
                item,
                if (width >= height) x + i * subWidth else x,
                if (width >= height) y else y + i * subHeight,
                subWidth,
                subHeight,
            )
        }
    }

    var bestI = 1
    var bestScore = Float.MAX_VALUE
    var currentLeftSum = 0.0
    var bestLeftSum = 0.0

    for (i in 1 until items.size) {
        currentLeftSum += items[i - 1].percentage.toDouble()

        val score =
            if (width >= height) {
                val lw = (width * currentLeftSum / total).toFloat()
                val rw = width - lw
                val leftRatio = if (lw > 0 && height > 0) maxOf(lw / height, height / lw) else Float.POSITIVE_INFINITY
                val rightRatio = if (rw > 0 && height > 0) maxOf(rw / height, height / rw) else Float.POSITIVE_INFINITY
                maxOf(leftRatio, rightRatio)
            } else {
                val lh = (height * currentLeftSum / total).toFloat()
                val rh = height - lh
                val topRatio = if (lh > 0 && width > 0) maxOf(lh / width, width / lh) else Float.POSITIVE_INFINITY
                val bottomRatio = if (rh > 0 && width > 0) maxOf(rh / width, width / rh) else Float.POSITIVE_INFINITY
                maxOf(topRatio, bottomRatio)
            }

        if (score < bestScore) {
            bestScore = score
            bestI = i
            bestLeftSum = currentLeftSum
        }
    }

    val leftItems = items.subList(0, bestI)
    val rightItems = items.subList(bestI, items.size)
    val pad = 3f

    val result = mutableListOf<TreemapRect>()
    if (width >= height) {
        val lw = (width * bestLeftSum / total).toFloat()
        result.addAll(computeTreemapLayout(leftItems, x, y, maxOf(0f, lw - pad), height))
        result.addAll(computeTreemapLayout(rightItems, x + lw, y, maxOf(0f, width - lw), height))
    } else {
        val lh = (height * bestLeftSum / total).toFloat()
        result.addAll(computeTreemapLayout(leftItems, x, y, width, maxOf(0f, lh - pad)))
        result.addAll(computeTreemapLayout(rightItems, x, y + lh, width, maxOf(0f, height - lh)))
    }
    return result
}

@Preview(name = "Normal")
@Composable
private fun MarketCapTreemapPreview() {
    val items =
        listOf(
            TreemapItem("AAPL", 30f),
            TreemapItem("MSFT", 22f),
            TreemapItem("NVDA", 18f),
            TreemapItem("GOOGL", 12f),
            TreemapItem("AMZN", 10f),
            TreemapItem("META", 8f),
        )

    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .background(Background),
    ) {
        MarketCapTreemap(items = items, otherLabel = "Other")
    }
}

@Preview(name = "With Other Merge")
@Composable
private fun MarketCapTreemapWithOtherPreview() {
    val items =
        listOf(
            TreemapItem("AAPL", 30f),
            TreemapItem("MSFT", 20f),
            TreemapItem("NVDA", 15f),
            TreemapItem("GOOGL", 10f),
            TreemapItem("AMZN", 8f),
            TreemapItem("META", 5f),
            TreemapItem("TSLA", 4f),
            TreemapItem("AVGO", 3f),
            TreemapItem("JPM", 2f),
            TreemapItem("V", 1.5f),
            TreemapItem("UNH", 1f),
            TreemapItem("XOM", 0.5f),
        )

    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .background(Background),
    ) {
        MarketCapTreemap(items = items, otherLabel = "Other")
    }
}

@Preview(name = "Single Item")
@Composable
private fun MarketCapTreemapSinglePreview() {
    val items =
        listOf(
            TreemapItem("AAPL", 100f),
        )

    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .background(Background),
    ) {
        MarketCapTreemap(items = items, otherLabel = "Other")
    }
}
