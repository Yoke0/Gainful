package com.yoke.gainful.sync

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.MarketRepository
import com.yoke.gainful.data.repository.QuoteCacheRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.data.repository.UserPreferencesRepository
import com.yoke.gainful.domain.usecase.holding.GetHoldingsDisplayUseCase
import com.yoke.gainful.model.QuoteSnapshot
import com.yoke.gainful.widget.domain.GetTodayPnlUseCase
import com.yoke.gainful.widget.syncWidgetData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

class StockPriceFetchService(
    private val assetRepository: AssetRepository,
    private val marketRepository: MarketRepository,
    private val quoteCacheRepository: QuoteCacheRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val transactionRepository: TransactionRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var fetchJob: Job? = null

    var isRunning: Boolean = false
        private set

    fun start() {
        if (isRunning) return
        isRunning = true
        fetchJob =
            scope.launch {
                while (currentCoroutineContext().isActive) {
                    val prefs = userPreferencesRepository.userPreferences.first()
                    val now = Clock.System.now()
                    val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
                    val time = localDateTime.time
                    val openTime = LocalTime(prefs.openHour, prefs.openMinute)
                    val closeTime = LocalTime(prefs.closeHour, prefs.closeMinute)

                    if (time in openTime..closeTime) {
                        fetchAllHoldings()
                    }

                    delay(prefs.refreshMinutes.minutes)
                }
            }
    }

    fun stop() {
        isRunning = false
        fetchJob?.cancel()
        fetchJob = null
    }

    suspend fun fetchOnce(): Boolean =
        runCatching {
            val assets = assetRepository.getAssets().first()
            val holdings = assets.filter { it.quoteId.isNotBlank() }
            if (holdings.isEmpty()) return@runCatching false
            fetchAllHoldings()
            true
        }.getOrDefault(false)

    private suspend fun fetchAllHoldings() {
        val assets = runCatching { assetRepository.getAssets().first() }.getOrNull() ?: return
        val holdings = assets.filter { it.quoteId.isNotBlank() }

        val snapshots =
            coroutineScope {
                holdings.map { asset ->
                    async { fetchSingleQuote(asset.quoteId) }
                }.mapNotNull { it.await() }
            }

        if (snapshots.isNotEmpty()) {
            quoteCacheRepository.upsertAll(snapshots)
            syncWidgetDataIfNeeded()
        }
    }

    private suspend fun syncWidgetDataIfNeeded() {
        runCatching {
            val holdingsUseCase = GetHoldingsDisplayUseCase(transactionRepository, assetRepository, marketRepository, quoteCacheRepository)
            val useCase = GetTodayPnlUseCase(holdingsUseCase)
            val pnl = useCase.compute(title = "", noDataText = "")
            syncWidgetData(pnl)
        }
    }

    private suspend fun fetchSingleQuote(quoteId: String): QuoteSnapshot? =
        runCatching {
            coroutineScope {
                val quoteDeferred = async { marketRepository.getQuote(quoteId) }
                val trendsDeferred = async { marketRepository.getTrends(quoteId, ndays = 1) }
                val quote = quoteDeferred.await() ?: return@coroutineScope null
                val trends = trendsDeferred.await()
                QuoteSnapshot(
                    quoteId = quoteId,
                    code = quote.code,
                    name = quote.name,
                    quote = quote,
                    trends = trends,
                    lastUpdated = Clock.System.now().toEpochMilliseconds(),
                )
            }
        }.getOrNull()
}
