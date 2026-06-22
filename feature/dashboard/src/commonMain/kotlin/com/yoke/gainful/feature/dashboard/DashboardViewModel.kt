package com.yoke.gainful.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.domain.usecase.holding.GetHoldingsDisplayUseCase
import com.yoke.gainful.domain.usecase.transaction.GetTransactionsUseCase
import com.yoke.gainful.model.HoldingDisplay
import com.yoke.gainful.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getHoldingsDisplayUseCase: GetHoldingsDisplayUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        onIntent(DashboardIntent.LoadPortfolioSummary)
    }

    fun onIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadPortfolioSummary -> loadData()
            is DashboardIntent.Refresh -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getHoldingsDisplayUseCase(),
                getTransactionsUseCase(),
            ) { holdings, transactions ->
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

                DashboardUiState(
                    holdings = holdings,
                    totalBuys = totalBuys,
                    totalSells = totalSells,
                    totalDividends = totalDividends,
                )
            }.collect { state ->
                _uiState.value = state
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
    val error: String? = null,
) {
    val totalMarketValue: Double get() = holdings.sumOf { it.totalMarketValue }
    val totalCost: Double get() = holdings.sumOf { it.totalCost }
    val totalGain: Double get() = -totalBuys + totalSells + totalDividends + totalMarketValue
    val totalGainPercent: Double
        get() = if (totalBuys > 0) (totalGain / totalBuys) * 100 else 0.0
}
