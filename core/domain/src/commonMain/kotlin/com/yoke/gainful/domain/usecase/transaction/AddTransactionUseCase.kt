package com.yoke.gainful.domain.usecase.transaction

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.model.Asset
import com.yoke.gainful.model.Transaction

class AddTransactionUseCase(
    private val transactionRepository: TransactionRepository,
    private val assetRepository: AssetRepository,
) {
    suspend operator fun invoke(transaction: Transaction, asset: Asset) {
        assetRepository.insertAsset(asset)
        transactionRepository.insertTransaction(transaction)
    }
}
