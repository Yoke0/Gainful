package com.yoke.gainful.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.domain.usecase.portfolio.GetPortfolioSummaryUseCase
import com.yoke.gainful.model.PortfolioSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getPortfolioSummaryUseCase: GetPortfolioSummaryUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadPortfolioSummary()
    }

    private fun loadPortfolioSummary() {
        viewModelScope.launch {
            getPortfolioSummaryUseCase().collect { summary ->
                _uiState.value = DashboardUiState(portfolioSummary = summary)
            }
        }
    }
}

data class DashboardUiState(
    val isLoading: Boolean = false,
    val portfolioSummary: PortfolioSummary? = null,
    val error: String? = null,
)
