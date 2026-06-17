package com.yoke.gainful.domain.usecase

import com.yoke.gainful.data.repository.AssetRepository
import com.yoke.gainful.data.repository.TransactionRepository
import com.yoke.gainful.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetTransactionsWithAssetsUseCase(
    private val transactionRepository: TransactionRepository,
    private val assetRepository: AssetRepository,
) {
    operator fun invoke(): Flow<List<TransactionWithAsset>> {
        return combine(
            transactionRepository.getTransactions(),
            assetRepository.getAssets(),
        ) { transactions, assets ->
            val assetMap = assets.associateBy { it.unifiedCode.ifBlank { it.code } }
            transactions.map { tx ->
                val asset = assetMap[tx.assetId]
                TransactionWithAsset(
                    transaction = tx,
                    code = asset?.code ?: tx.assetId,
                    name = asset?.name ?: tx.assetId,
                )
            }
        }
    }
}

data class TransactionWithAsset(
    val transaction: Transaction,
    val code: String,
    val name: String,
)
