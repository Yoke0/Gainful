package com.yoke.gainful.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yoke.gainful.database.dao.AssetDao
import com.yoke.gainful.database.dao.TransactionDao
import com.yoke.gainful.database.model.AssetEntity
import com.yoke.gainful.database.model.TransactionEntity

@Database(
    entities = [AssetEntity::class, TransactionEntity::class],
    version = 1
)
abstract class GainfulDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun transactionDao(): TransactionDao
}
