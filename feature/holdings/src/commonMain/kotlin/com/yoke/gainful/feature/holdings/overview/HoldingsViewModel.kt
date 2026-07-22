package com.yoke.gainful.feature.holdings.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.domain.usecase.holding.GetClosedPositionsUseCase
import com.yoke.gainful.domain.usecase.holding.GetHoldingsDisplayUseCase
import com.yoke.gainful.model.ClosedPosition
import com.yoke.gainful.model.HoldingDisplay
import com.yoke.gainful.ui.EventChannel
import com.yoke.gainful.ui.SnackbarEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HoldingsViewModel(
    private val getHoldingsDisplayUseCase: GetHoldingsDisplayUseCase,
    private val getClosedPositionsUseCase: GetClosedPositionsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HoldingsUiState())
    val uiState: StateFlow<HoldingsUiState> = _uiState.asStateFlow()

    private val _events = EventChannel<SnackbarEvent>()
    val events = _events.events

    init {
        onIntent(HoldingsIntent.LoadHoldings)
    }

    fun onIntent(intent: HoldingsIntent) {
        when (intent) {
            is HoldingsIntent.LoadHoldings -> loadHoldings()
            is HoldingsIntent.Refresh -> loadHoldings()
        }
    }

    private fun loadHoldings() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            runCatching {
                getHoldingsDisplayUseCase().collect { holdings ->
                    val totalValue = holdings.sumOf { it.totalMarketValue }
                    val totalPnl = holdings.sumOf { it.totalGain }
                    val totalBuys = holdings.sumOf { it.totalBuys }
                    val totalPnlPct = if (totalBuys > 0) (totalPnl / totalBuys) * 100 else 0.0
                    _uiState.value =
                        _uiState.value.copy(
                            holdings = holdings,
                            totalValue = totalValue,
                            totalPnl = totalPnl,
                            totalPnlPct = totalPnlPct,
                            isLoading = false,
                        )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(SnackbarEvent.Error())
            }
        }
        viewModelScope.launch {
            runCatching {
                getClosedPositionsUseCase().collect { closed ->
                    _uiState.value = _uiState.value.copy(closedPositions = closed)
                }
            }
        }
    }
}

data class HoldingsUiState(
    val isLoading: Boolean = false,
    val holdings: List<HoldingDisplay> = emptyList(),
    val closedPositions: List<ClosedPosition> = emptyList(),
    val totalValue: Double = 0.0,
    val totalPnl: Double = 0.0,
    val totalPnlPct: Double = 0.0,
)
