package com.yoke.gainful.data.repository

import com.yoke.gainful.model.Holding
import kotlinx.coroutines.flow.Flow

interface HoldingRepository {
    fun getHoldings(): Flow<List<Holding>>
    suspend fun getHoldingById(id: String): Holding?
    suspend fun getHoldingByAssetId(assetId: String): Holding?
    suspend fun insertHolding(holding: Holding)
    suspend fun updateHolding(holding: Holding)
    suspend fun deleteHolding(id: String)
}
