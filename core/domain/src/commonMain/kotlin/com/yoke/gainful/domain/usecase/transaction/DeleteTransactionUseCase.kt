package com.yoke.gainful.domain.usecase.transaction

import com.yoke.gainful.data.repository.PnlCacheRepository
import com.yoke.gainful.data.repository.SyncQueueRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.data.repository.TransactionSyncRepository
import com.yoke.gainful.datastore.AuthDataSource
import kotlinx.coroutines.flow.first

class DeleteTransactionUseCase(
    private val transactionRepository: TransactionRepository,
    private val pnlCacheRepository: PnlCacheRepository,
    private val syncQueueRepository: SyncQueueRepository,
    private val transactionSyncRepository: TransactionSyncRepository,
    private val authDataSource: AuthDataSource,
) {
    suspend operator fun invoke(id: String) {
        transactionRepository.deleteTransaction(id)
        pnlCacheRepository.clear()
        // Try delete on server immediately
        val state = authDataSource.authState.first()
        val token = state.token
        if (token != null) {
            val result =
                runCatching {
                    transactionSyncRepository.deleteTransaction(token, id)
                }
            if (result.isSuccess) return
        }
        // Fallback: enqueue for background sync
        syncQueueRepository.enqueue("transaction", id, "DELETE")
    }
}
