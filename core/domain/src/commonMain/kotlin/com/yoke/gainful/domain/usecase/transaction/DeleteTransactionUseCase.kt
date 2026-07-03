package com.yoke.gainful.domain.usecase.transaction

import com.yoke.gainful.data.repository.PnlCacheRepository
import com.yoke.gainful.data.repository.TransactionRepository

class DeleteTransactionUseCase(
    private val transactionRepository: TransactionRepository,
    private val pnlCacheRepository: PnlCacheRepository,
) {
    suspend operator fun invoke(id: String) {
        transactionRepository.deleteTransaction(id)
        pnlCacheRepository.clear()
    }
}
