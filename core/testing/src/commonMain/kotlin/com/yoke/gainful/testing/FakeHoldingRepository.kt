package com.yoke.gainful.testing

import com.yoke.gainful.data.repository.HoldingRepository
import com.yoke.gainful.model.Holding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeHoldingRepository : HoldingRepository {
    private val holdings = MutableStateFlow<List<Holding>>(emptyList())

    override fun getHoldings(): Flow<List<Holding>> = holdings

    override suspend fun getHoldingById(id: String): Holding? {
        return holdings.value.find { it.id == id }
    }

    override suspend fun getHoldingByAssetId(assetId: String): Holding? {
        return holdings.value.find { it.assetId == assetId }
    }

    override suspend fun insertHolding(holding: Holding) {
        holdings.value = holdings.value + holding
    }

    override suspend fun updateHolding(holding: Holding) {
        holdings.value = holdings.value.map {
            if (it.id == holding.id) holding else it
        }
    }

    override suspend fun deleteHolding(id: String) {
        holdings.value = holdings.value.filter { it.id != id }
    }
}
