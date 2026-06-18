package com.yoke.gainful.feature.holdings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.domain.usecase.GetHoldingsDisplayUseCase
import com.yoke.gainful.model.HoldingDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HoldingsViewModel(
    private val getHoldingsDisplayUseCase: GetHoldingsDisplayUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HoldingsUiState())
    val uiState: StateFlow<HoldingsUiState> = _uiState.asStateFlow()

    init {
        loadHoldings()
    }

    private fun loadHoldings() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            getHoldingsDisplayUseCase().collect { holdings ->
                _uiState.value = HoldingsUiState(
                    holdings = holdings,
                    isLoading = false,
                )
            }
        }
    }
}

data class HoldingsUiState(
    val isLoading: Boolean = false,
    val holdings: List<HoldingDisplay> = emptyList(),
    val error: String? = null,
)
