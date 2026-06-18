package com.yoke.gainful.feature.holdings.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.domain.usecase.holding.GetStockDetailUseCase
import com.yoke.gainful.model.ChartPeriod
import com.yoke.gainful.model.KLine
import com.yoke.gainful.model.KLinePeriod
import com.yoke.gainful.model.StockQuote
import com.yoke.gainful.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StockDetailViewModel(
    private val getStockDetailUseCase: GetStockDetailUseCase,
    private val stockCode: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StockDetailUiState())
    val uiState: StateFlow<StockDetailUiState> = _uiState.asStateFlow()

    private var quoteId: String? = null

    init {
        loadData()
    }

    private fun loadData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val result = getStockDetailUseCase(stockCode)
                quoteId = result.quoteId
                _uiState.value = StockDetailUiState(
                    code = result.code,
                    name = result.quote?.name ?: result.name,
                    fullName = result.quote?.name ?: result.name,
                    quote = result.quote,
                    quantity = result.quantity,
                    averageCost = result.averageCost,
                    transactions = result.transactions,
                    kLines = result.kLines,
                    isLoading = false,
                )
            } catch (e: Exception) {
                _uiState.value = StockDetailUiState(
                    code = stockCode,
                    name = stockCode,
                    fullName = stockCode,
                    isLoading = false,
                    error = e.message,
                )
            }
        }
    }

    fun loadChart(period: ChartPeriod) {
        val qid = quoteId ?: return
        viewModelScope.launch {
            try {
                val kLines = if (period.isTrends) {
                    emptyList()
                } else {
                    getStockDetailUseCase.fetchKLines(qid, period)
                }
                _uiState.value = _uiState.value.copy(kLines = kLines)
            } catch (_: Exception) {
            }
        }
    }
}

data class StockDetailUiState(
    val code: String = "",
    val name: String = "",
    val fullName: String = "",
    val quote: StockQuote? = null,
    val quantity: Double = 0.0,
    val averageCost: Double = 0.0,
    val transactions: List<Transaction> = emptyList(),
    val kLines: List<KLine> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val price: Double
        get() = quote?.latestPrice ?: 0.0

    val open: Double
        get() = quote?.open ?: 0.0

    val high: Double
        get() = quote?.high ?: 0.0

    val low: Double
        get() = quote?.low ?: 0.0

    val volume: Long
        get() = quote?.volume ?: 0L

    val totalMarketCap: Double
        get() = quote?.totalMarketCap ?: 0.0

    val peDynamic: Double
        get() = quote?.peDynamic ?: 0.0

    val turnoverRate: Double
        get() = quote?.turnoverRate ?: 0.0

    val changePercent: Double
        get() = quote?.changePercent ?: 0.0

    val changeAmount: Double
        get() = quote?.changeAmount ?: 0.0

    val totalMarketValue: Double
        get() = price * quantity

    val totalGain: Double
        get() = totalMarketValue - (averageCost * quantity)

    val totalGainPercent: Double
        get() = if (averageCost > 0) ((price - averageCost) / averageCost) * 100 else 0.0

    val pb: Double
        get() = quote?.pb ?: 0.0

    val industry: String
        get() = quote?.industry ?: ""

    val preClose: Double
        get() = quote?.preClose ?: 0.0

    val circulatingMarketCap: Double
        get() = quote?.circulatingMarketCap ?: 0.0

    val turnover: Double
        get() = quote?.turnover ?: 0.0

    val amplitude: Double
        get() = quote?.amplitude ?: 0.0
}
