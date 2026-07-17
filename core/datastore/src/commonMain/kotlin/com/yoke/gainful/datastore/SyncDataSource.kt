package com.yoke.gainful.datastore

import androidx.datastore.core.DataStore
import com.yoke.gainful.proto.GainfulDataProto
import com.yoke.gainful.proto.SyncStateProto
import kotlinx.coroutines.flow.first

class SyncDataSource(
    private val dataStore: DataStore<GainfulDataProto>,
) {
    suspend fun getLastTransactionSyncTime(): Long =
        dataStore.data.first().sync_state?.last_transaction_sync_time ?: 0L

    suspend fun setLastTransactionSyncTime(time: Long) {
        dataStore.updateData { data ->
            val sync = data.sync_state ?: SyncStateProto()
            data.copy(sync_state = sync.copy(last_transaction_sync_time = time))
        }
    }

    suspend fun clearSyncState() {
        dataStore.updateData { data ->
            val sync = data.sync_state ?: SyncStateProto()
            data.copy(sync_state = sync.copy(last_transaction_sync_time = 0))
        }
    }
}
