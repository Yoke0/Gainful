package com.yoke.gainful.feature.holdings.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoke.gainful.domain.usecase.holding.GetStockDetailUseCase
import com.yoke.gainful.model.ChartPeriod
import com.yoke.gainful.model.KLine
import com.yoke.gainful.model.StockQuote
import com.yoke.gainful.model.Transaction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

class StockDetailViewModel(
    private val getStockDetailUseCase: GetStockDetailUseCase,
    private val stockCode: String,
    stockName: String,
    stockPinYin: String,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<StockDetailUiState>(
            StockDetailUiState.Loading(code = stockCode, name = stockName, pinYin = stockPinYin),
        )
    val uiState: StateFlow<StockDetailUiState> = _uiState.asStateFlow()

    private var quoteId: String? = null

    init {
        onIntent(StockDetailIntent.LoadData)
    }

    fun onIntent(intent: StockDetailIntent) {
        when (intent) {
            is StockDetailIntent.LoadData -> loadData()
            is StockDetailIntent.SelectPeriod -> selectPeriod(intent.period)
            is StockDetailIntent.Retry -> loadData()
        }
    }

    private fun loadData() {
        val current = _uiState.value
        val (name, pinYin) =
            when (current) {
                is StockDetailUiState.Loading -> current.name to current.pinYin
                is StockDetailUiState.Error -> current.name to current.pinYin
                is StockDetailUiState.Success -> current.name to current.pinYin
            }
        _uiState.value = StockDetailUiState.Loading(code = stockCode, name = name, pinYin = pinYin)
        viewModelScope.launch {
            val result =
                runCatching {
                    val start = TimeSource.Monotonic.markNow()
                    val data = getStockDetailUseCase(stockCode)
                    delay((MIN_LOADING_MS - start.elapsedNow().inWholeMilliseconds).coerceAtLeast(0).milliseconds)
                    data
                }
            result.onSuccess { data ->
                quoteId = data.quoteId
                _uiState.value =
                    StockDetailUiState.Success(
                        code = data.code,
                        name = data.quote?.name ?: data.name,
                        pinYin = data.pinYin,
                        quote = data.quote,
                        quantity = data.quantity,
                        averageCost = data.averageCost,
                        totalBuys = data.totalBuys,
                        totalSells = data.totalSells,
                        totalDividends = data.totalDividends,
                        transactions = data.transactions,
                        kLines = data.kLines,
                    )
            }.onFailure { e ->
                _uiState.value =
                    StockDetailUiState.Error(
                        code = stockCode,
                        name = name,
                        pinYin = pinYin,
                        errorMessage = e.message ?: "Unknown error",
                    )
            }
        }
    }

    private fun selectPeriod(period: ChartPeriod) {
        val current = _uiState.value
        if (current !is StockDetailUiState.Success) return
        val qid = quoteId ?: return
        _uiState.value = current.copy(selectedPeriod = period)
        viewModelScope.launch {
            val kLines =
                runCatching {
                    if (period.isTrends) {
                        emptyList()
                    } else {
                        getStockDetailUseCase.fetchKLines(qid, period)
                    }
                }.getOrNull() ?: return@launch
            _uiState.value =
                _uiState.value.let { state ->
                    if (state is StockDetailUiState.Success) state.copy(kLines = kLines) else state
                }
        }
    }

    companion object {
        private const val MIN_LOADING_MS = 800L
    }
}

sealed interface StockDetailUiState {
    val code: String
    val name: String
    val pinYin: String

    data class Loading(
        override val code: String,
        override val name: String,
        override val pinYin: String,
    ) : StockDetailUiState

    data class Error(
        override val code: String,
        override val name: String,
        override val pinYin: String,
        val errorMessage: String,
    ) : StockDetailUiState

    data class Success(
        override val code: String,
        override val name: String,
        override val pinYin: String,
        val quote: StockQuote? = null,
        val quantity: Double = 0.0,
        val averageCost: Double = 0.0,
        val totalBuys: Double = 0.0,
        val totalSells: Double = 0.0,
        val totalDividends: Double = 0.0,
        val transactions: List<Transaction> = emptyList(),
        val kLines: List<KLine> = emptyList(),
        val selectedPeriod: ChartPeriod = ChartPeriod.DAILY,
    ) : StockDetailUiState {
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

        val turnoverRate: Double
            get() = quote?.turnoverRate ?: 0.0

        val changePercent: Double
            get() = quote?.changePercent ?: 0.0

        val changeAmount: Double
            get() = quote?.changeAmount ?: 0.0

        val totalMarketValue: Double
            get() = price * quantity

        val totalGain: Double
            get() = -totalBuys + totalSells + totalDividends + totalMarketValue

        val turnover: Double
            get() = quote?.turnover ?: 0.0

        val amplitude: Double
            get() = quote?.amplitude ?: 0.0
    }
}
