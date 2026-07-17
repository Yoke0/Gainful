package com.yoke.gainful.domain.usecase.transaction

import com.yoke.gainful.data.repository.PnlCacheRepository
import com.yoke.gainful.data.repository.SyncQueueRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.data.repository.TransactionSyncRepository
import com.yoke.gainful.datastore.UserDataSource
import kotlinx.coroutines.flow.first

class DeleteTransactionUseCase(
    private val transactionRepository: TransactionRepository,
    private val pnlCacheRepository: PnlCacheRepository,
    private val syncQueueRepository: SyncQueueRepository,
    private val transactionSyncRepository: TransactionSyncRepository,
    private val userDataSource: UserDataSource,
) {
    suspend operator fun invoke(id: String) {
        transactionRepository.deleteTransaction(id)
        pnlCacheRepository.clear()
        // Try delete on server immediately
        val state = userDataSource.userState.first()
        if (state.isLoggedIn) {
            val result =
                runCatching {
                    transactionSyncRepository.deleteTransaction(id)
                }
            if (result.isSuccess) return
        }
        // Fallback: enqueue for background sync
        syncQueueRepository.enqueue("transaction", id, "DELETE")
    }
}
