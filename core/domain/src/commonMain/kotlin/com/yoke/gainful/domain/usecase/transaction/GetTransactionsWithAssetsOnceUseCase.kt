package com.yoke.gainful.domain.usecase.transaction

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.TransactionRepository
import kotlinx.coroutines.flow.first

class GetTransactionsWithAssetsOnceUseCase(
    private val transactionRepository: TransactionRepository,
    private val assetRepository: AssetRepository,
) {
    suspend operator fun invoke(): List<TransactionWithAsset> {
        val transactions = transactionRepository.getAllTransactions()
        val assets = assetRepository.getAssets().first()
        val assetMap = assets.associateBy { it.unifiedCode.ifBlank { it.code } }
        return transactions.map { tx ->
            val asset = assetMap[tx.assetId]
            TransactionWithAsset(
                transaction = tx,
                code = asset?.code ?: tx.assetId,
                name = asset?.name ?: tx.assetId,
                pinYin = asset?.pinYin ?: "",
            )
        }
    }
}
