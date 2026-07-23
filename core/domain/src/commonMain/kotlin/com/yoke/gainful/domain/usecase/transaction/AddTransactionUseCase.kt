package com.yoke.gainful.domain.usecase.transaction

import com.yoke.gainful.api.CreateTransactionRequest
import com.yoke.gainful.common.extensions.formatLocalizedDateTime
import com.yoke.gainful.data.repository.PnlCacheRepository
import com.yoke.gainful.data.repository.SyncQueueRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.data.repository.TransactionSyncRepository
import com.yoke.gainful.datastore.UserDataSource
import com.yoke.gainful.model.Transaction
import kotlinx.coroutines.flow.first

class AddTransactionUseCase(
    private val transactionRepository: TransactionRepository,
    private val pnlCacheRepository: PnlCacheRepository,
    private val syncQueueRepository: SyncQueueRepository,
    private val transactionSyncRepository: TransactionSyncRepository,
    private val userDataSource: UserDataSource,
) {
    suspend operator fun invoke(transaction: Transaction) {
        transactionRepository.insertTransaction(transaction)
        pnlCacheRepository.clear()
        // Try upload to server immediately
        val state = userDataSource.userState.first()
        if (state.isLoggedIn) {
            val result =
                runCatching {
                    transactionSyncRepository.createTransaction(transaction.toCreateRequest())
                }
            if (result.isSuccess) return
        }
        // Fallback: enqueue for background sync
        syncQueueRepository.enqueue("transaction", transaction.id, "CREATE")
    }
}

private fun Transaction.toCreateRequest() =
    CreateTransactionRequest(
        assetCode = assetId,
        type = type.value,
        quantity = quantity,
        price = price,
        amount = amount,
        tradeDate = tradeDate.formatLocalizedDateTime(),
    )
