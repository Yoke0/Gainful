package com.yoke.gainful.domain.usecase.transaction

import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.model.Transaction

class AddTransactionUseCase(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(transaction: Transaction) {
        transactionRepository.insertTransaction(transaction)
    }
}
