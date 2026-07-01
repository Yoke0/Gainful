package com.yoke.gainful.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.domain.usecase.dashboard.GetPnlDataUseCase
import com.yoke.gainful.domain.usecase.holding.GetHoldingsDisplayUseCase
import com.yoke.gainful.domain.usecase.transaction.GetTransactionsUseCase
import com.yoke.gainful.model.HoldingDisplay
import com.yoke.gainful.model.PnlData
import com.yoke.gainful.model.PnlPeriodType
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

class DashboardViewModel(
    private val getHoldingsDisplayUseCase: GetHoldingsDisplayUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getPnlDataUseCase: GetPnlDataUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState.now())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var allTransactions: List<Transaction> = emptyList()
    private val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    init {
        onIntent(DashboardIntent.LoadPortfolioSummary)
    }

    fun onIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadPortfolioSummary -> loadData()
            is DashboardIntent.Refresh -> loadData()
            is DashboardIntent.SelectPnlPeriod -> selectPnlPeriod(intent.periodType)
            is DashboardIntent.NavigatePnlPeriod -> navigatePnlPeriod(intent.direction)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getHoldingsDisplayUseCase(),
                getTransactionsUseCase(),
            ) { holdings, transactions ->
                allTransactions = transactions

                var totalBuys = 0.0
                var totalSells = 0.0
                var totalDividends = 0.0

                transactions.forEach { tx ->
                    when (tx.type) {
                        TransactionType.BUY -> totalBuys += tx.amount
                        TransactionType.SELL -> totalSells += tx.amount
                        TransactionType.DIVIDEND -> totalDividends += tx.amount
                    }
                }

                val currentState = _uiState.value
                val mockTransactions = generateMockTransactions()
                val pnlData =
                    getPnlDataUseCase(
                        transactions = mockTransactions,
                        periodType = currentState.selectedPnlPeriod,
                        year = currentState.pnlYear,
                        month = currentState.pnlMonth,
                    )

                DashboardUiState(
                    holdings = holdings,
                    totalBuys = totalBuys,
                    totalSells = totalSells,
                    totalDividends = totalDividends,
                    selectedPnlPeriod = currentState.selectedPnlPeriod,
                    pnlYear = currentState.pnlYear,
                    pnlMonth = currentState.pnlMonth,
                    pnlData = pnlData,
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun selectPnlPeriod(periodType: PnlPeriodType) {
        val currentState = _uiState.value

        viewModelScope.launch {
            val mockTransactions = generateMockTransactions()
            val pnlData =
                getPnlDataUseCase(
                    transactions = mockTransactions,
                    periodType = periodType,
                    year = currentState.pnlYear,
                    month = currentState.pnlMonth,
                )
            _uiState.value =
                currentState.copy(
                    selectedPnlPeriod = periodType,
                    pnlData = pnlData,
                )
        }
    }

    private fun navigatePnlPeriod(direction: Int) {
        val currentState = _uiState.value

        if (currentState.selectedPnlPeriod == PnlPeriodType.YEAR) return

        var newYear = currentState.pnlYear
        var newMonth = currentState.pnlMonth

        when (currentState.selectedPnlPeriod) {
            PnlPeriodType.DAY, PnlPeriodType.WEEK -> {
                newMonth += direction
                if (newMonth < 1) {
                    newYear -= 1
                    newMonth = 12
                } else if (newMonth > 12) {
                    newYear += 1
                    newMonth = 1
                }
                if (newYear > today.year || (newYear == today.year && newMonth > today.month.ordinal + 1)) {
                    return
                }
            }

            PnlPeriodType.MONTH -> {
                newYear += direction
            }

            PnlPeriodType.YEAR -> {
                return
            }
        }

        viewModelScope.launch {
            val mockTransactions = generateMockTransactions()
            val pnlData =
                getPnlDataUseCase(
                    transactions = mockTransactions,
                    periodType = currentState.selectedPnlPeriod,
                    year = newYear,
                    month = newMonth,
                )
            _uiState.value =
                currentState.copy(
                    pnlYear = newYear,
                    pnlMonth = newMonth,
                    pnlData = pnlData,
                )
        }
    }

    private fun generateMockTransactions(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val now = today

        for (dayOffset in 0..30) {
            val date = now.minus(dayOffset.toLong(), DateTimeUnit.DAY)
            if (date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY) {
                val tradeDate = date.toEpochMilliseconds()
                val random = kotlin.random.Random(dayOffset)

                if (random.nextInt(100) < 60) {
                    val amount = random.nextDouble(500.0, 5000.0)
                    transactions.add(
                        Transaction(
                            id = "mock_buy_$dayOffset",
                            assetId = "asset_${random.nextInt(1, 5)}",
                            type = TransactionType.BUY,
                            quantity = random.nextDouble(10.0, 100.0),
                            price = amount / 100.0,
                            amount = amount,
                            tradeDate = tradeDate,
                            timestamp = tradeDate,
                        ),
                    )
                }

                if (random.nextInt(100) < 30) {
                    val amount = random.nextDouble(300.0, 3000.0)
                    transactions.add(
                        Transaction(
                            id = "mock_sell_$dayOffset",
                            assetId = "asset_${random.nextInt(1, 5)}",
                            type = TransactionType.SELL,
                            quantity = random.nextDouble(5.0, 50.0),
                            price = amount / 50.0,
                            amount = amount,
                            tradeDate = tradeDate,
                            timestamp = tradeDate,
                        ),
                    )
                }

                if (random.nextInt(100) < 10) {
                    transactions.add(
                        Transaction(
                            id = "mock_div_$dayOffset",
                            assetId = "asset_${random.nextInt(1, 5)}",
                            type = TransactionType.DIVIDEND,
                            quantity = 0.0,
                            price = 0.0,
                            amount = random.nextDouble(10.0, 200.0),
                            tradeDate = tradeDate,
                            timestamp = tradeDate,
                        ),
                    )
                }
            }
        }

        return transactions
    }

    private fun LocalDate.toEpochMilliseconds(): Long {
        return this.toEpochDays() * 86_400_000L
    }
}

data class DashboardUiState(
    val isLoading: Boolean = false,
    val holdings: List<HoldingDisplay> = emptyList(),
    val totalBuys: Double = 0.0,
    val totalSells: Double = 0.0,
    val totalDividends: Double = 0.0,
    val selectedPnlPeriod: PnlPeriodType = PnlPeriodType.DAY,
    val pnlYear: Int = 0,
    val pnlMonth: Int = 0,
    val pnlData: PnlData? = null,
    val error: String? = null,
) {
    val totalMarketValue: Double get() = holdings.sumOf { it.totalMarketValue }
    val totalCost: Double get() = holdings.sumOf { it.totalCost }
    val totalGain: Double get() = -totalBuys + totalSells + totalDividends + totalMarketValue
    val totalGainPercent: Double
        get() = if (totalBuys > 0) (totalGain / totalBuys) * 100 else 0.0

    companion object {
        fun now(): DashboardUiState {
            val date = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            return DashboardUiState(pnlYear = date.year, pnlMonth = date.month.ordinal + 1)
        }
    }
}
