package com.yoke.gainful.feature.holdings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.domain.usecase.GetHoldingsUseCase
import com.yoke.gainful.model.Holding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HoldingsViewModel(
    private val getHoldingsUseCase: GetHoldingsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HoldingsUiState())
    val uiState: StateFlow<HoldingsUiState> = _uiState.asStateFlow()

    init {
        loadHoldings()
    }

    private fun loadHoldings() {
        viewModelScope.launch {
            getHoldingsUseCase().collect { holdings ->
                _uiState.value = HoldingsUiState(holdings = holdings)
            }
        }
    }
}

data class HoldingsUiState(
    val isLoading: Boolean = false,
    val holdings: List<Holding> = emptyList(),
    val error: String? = null,
)
