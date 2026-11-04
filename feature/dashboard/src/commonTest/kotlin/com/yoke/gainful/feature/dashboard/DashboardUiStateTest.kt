package com.yoke.gainful.feature.dashboard

import com.yoke.gainful.model.HoldingDisplay
import kotlin.test.Test
import kotlin.test.assertEquals

class DashboardUiStateTest {
    @Test
    fun totalCostIsNetInvestedCapitalFromCashflows() {
        val state =
            DashboardUiState(
                holdings =
                    listOf(
                        holding(
                            quantity = 100.0,
                            currentPrice = 12.0,
                            totalBuys = 1_000.0,
                        ),
                    ),
                totalBuys = 1_000.0,
            )

        assertEquals(1_000.0, state.totalCost)
        assertEquals(1_200.0, state.totalMarketValue)
        assertEquals(200.0, state.totalGain)
        assertEquals(1_000.0, state.holdings.single().totalCost)
    }

    @Test
    fun totalCostIncludesRealizedCashflowsFromClosedPositions() {
        // Closed position: bought 1_000, sold 1_100 (quantity back to 0, not in holdings).
        // Open position: bought 1_000, now worth 1_200.
        val state =
            DashboardUiState(
                holdings =
                    listOf(
                        holding(
                            quantity = 100.0,
                            currentPrice = 12.0,
                            totalBuys = 1_000.0,
                        ),
                    ),
                totalBuys = 2_000.0,
                totalSells = 1_100.0,
            )

        assertEquals(1_200.0, state.totalMarketValue)
        assertEquals(300.0, state.totalGain)
        assertEquals(900.0, state.totalCost)
    }

    @Test
    fun totalCostAggregatesAllHoldingsAndCashflows() {
        val state =
            DashboardUiState(
                holdings =
                    listOf(
                        holding(
                            quantity = 100.0,
                            currentPrice = 12.0,
                            totalBuys = 1_000.0,
                        ),
                        holding(
                            id = "600519",
                            assetId = "600519",
                            code = "600519",
                            quantity = 10.0,
                            currentPrice = 1_500.0,
                            totalBuys = 12_000.0,
                            totalSells = 3_000.0,
                            totalDividends = 200.0,
                        ),
                    ),
                totalBuys = 13_000.0,
                totalSells = 3_000.0,
                totalDividends = 200.0,
            )

        assertEquals(16_200.0, state.totalMarketValue)
        assertEquals(6_400.0, state.totalGain)
        assertEquals(9_800.0, state.totalCost)
    }

    private fun holding(
        id: String = "600000",
        assetId: String = "600000",
        code: String = "600000",
        quantity: Double,
        currentPrice: Double,
        totalBuys: Double,
        totalSells: Double = 0.0,
        totalDividends: Double = 0.0,
    ) = HoldingDisplay(
        id = id,
        assetId = assetId,
        code = code,
        name = "Test Stock",
        quantity = quantity,
        averageCost = (totalBuys - totalSells - totalDividends) / quantity,
        currentPrice = currentPrice,
        preClose = currentPrice,
        changePercent = 0.0,
        changeAmount = 0.0,
        totalBuys = totalBuys,
        totalSells = totalSells,
        totalDividends = totalDividends,
    )
}
