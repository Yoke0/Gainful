package com.yoke.gainful.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.domain.usecase.dashboard.GetPnlDataUseCase
import com.yoke.gainful.domain.usecase.holding.GetHoldingsDisplayUseCase
import com.yoke.gainful.domain.usecase.transaction.GetTransactionsUseCase
import com.yoke.gainful.model.HoldingDisplay
import com.yoke.gainful.model.PnlData
import com.yoke.gainful.model.PnlPeriodType
import com.yoke.gainful.model.StockPnlDetail
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.model.TransactionType
import com.yoke.gainful.widget.domain.GetTodayPnlUseCase
import com.yoke.gainful.widget.syncWidgetData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DashboardViewModel(
    private val getHoldingsDisplayUseCase: GetHoldingsDisplayUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getPnlDataUseCase: GetPnlDataUseCase,
    private val getTodayPnlUseCase: GetTodayPnlUseCase,
    private val assetRepository: AssetRepository,
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
            is DashboardIntent.SelectPnlCell -> selectPnlCell(intent.year, intent.month, intent.day)
            is DashboardIntent.DismissPnlDetail -> dismissPnlDetail()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getHoldingsDisplayUseCase(),
                getTransactionsUseCase(),
            ) { holdings, transactions ->
                allTransactions = transactions

                // Calculate first transaction year and month
                val firstTransaction = transactions.minByOrNull { it.tradeDate }
                val firstTransactionYear =
                    firstTransaction?.let { tx ->
                        kotlinx.datetime.Instant.fromEpochMilliseconds(tx.tradeDate)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date.year
                    } ?: 2023
                val firstTransactionMonth =
                    firstTransaction?.let { tx ->
                        kotlinx.datetime.Instant.fromEpochMilliseconds(tx.tradeDate)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date.month.ordinal + 1
                    } ?: 8

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
                val pnlData =
                    getPnlDataUseCase(
                        transactions = transactions,
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
                    firstTransactionYear = firstTransactionYear,
                    firstTransactionMonth = firstTransactionMonth,
                )
            }.collect { state ->
                _uiState.value = state
                syncWidgetDataIfNeeded()
            }
        }
    }

    private fun selectPnlPeriod(periodType: PnlPeriodType) {
        val currentState = _uiState.value

        viewModelScope.launch {
            val pnlData =
                getPnlDataUseCase(
                    transactions = allTransactions,
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
            val pnlData =
                getPnlDataUseCase(
                    transactions = allTransactions,
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

    private fun selectPnlCell(year: Int, month: Int, day: Int) {
        val currentState = _uiState.value
        val date = LocalDate(year, month, day)

        viewModelScope.launch {
            // Get stock names from both holdings and asset repository
            val holdingsNames = currentState.holdings.associate { it.code to it.name }
            val assets = assetRepository.getAssets().firstOrNull() ?: emptyList()
            val assetNames = assets.associate { it.code to it.name }
            val stockNames = holdingsNames + assetNames

            val details =
                getPnlDataUseCase.getStockPnlDetails(
                    transactions = allTransactions,
                    date = date,
                    stockNames = stockNames,
                )
            _uiState.value =
                currentState.copy(
                    selectedPnlDate = date,
                    stockPnlDetails = details,
                )
        }
    }

    private fun dismissPnlDetail() {
        _uiState.value =
            _uiState.value.copy(
                selectedPnlDate = null,
                stockPnlDetails = emptyList(),
            )
    }

    private fun syncWidgetDataIfNeeded() {
        viewModelScope.launch {
            runCatching {
                val pnl = getTodayPnlUseCase.compute(title = "", noDataText = "")
                syncWidgetData(pnl)
            }
        }
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
    val selectedPnlDate: LocalDate? = null,
    val stockPnlDetails: List<StockPnlDetail> = emptyList(),
    val firstTransactionYear: Int = 2023,
    val firstTransactionMonth: Int = 8,
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
