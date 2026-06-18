package com.yoke.gainful.domain.usecase.transaction

import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.model.Transaction
import kotlinx.coroutines.flow.Flow

class GetTransactionsUseCase(
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(): Flow<List<Transaction>> {
        return transactionRepository.getTransactions()
    }
}
