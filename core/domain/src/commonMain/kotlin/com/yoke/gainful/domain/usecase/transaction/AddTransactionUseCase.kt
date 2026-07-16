package com.yoke.gainful.domain.usecase.transaction

import com.yoke.gainful.data.repository.PnlCacheRepository
import com.yoke.gainful.data.repository.SyncQueueRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.datastore.AuthDataSource
import com.yoke.gainful.model.Transaction
import com.yoke.gainful.network.TransactionApi
import com.yoke.gainful.network.model.CreateTransactionRequestDto
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AddTransactionUseCase(
    private val transactionRepository: TransactionRepository,
    private val pnlCacheRepository: PnlCacheRepository,
    private val syncQueueRepository: SyncQueueRepository,
    private val transactionApi: TransactionApi,
    private val authDataSource: AuthDataSource,
) {
    suspend operator fun invoke(transaction: Transaction) {
        transactionRepository.insertTransaction(transaction)
        pnlCacheRepository.clear()
        // Try upload to server immediately
        val state = authDataSource.authState.first()
        val token = state.token
        if (token != null) {
            val result =
                runCatching {
                    transactionApi.createTransaction(token, transaction.toCreateRequest())
                }
            if (result.isSuccess) return
        }
        // Fallback: enqueue for background sync
        syncQueueRepository.enqueue("transaction", transaction.id, "CREATE")
    }
}

private fun Transaction.toCreateRequest() =
    CreateTransactionRequestDto(
        assetCode = assetId,
        type = type.value,
        quantity = quantity,
        price = price,
        amount = amount,
        tradeDate = formatDate(tradeDate),
    )

private fun formatDate(millis: Long): String {
    val ldt =
        kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    return ldt.date.toString()
}
