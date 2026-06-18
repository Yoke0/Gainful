package com.yoke.gainful.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.yoke.gainful.database.dao.AssetDao
import com.yoke.gainful.database.dao.TransactionDao
import com.yoke.gainful.database.model.AssetEntity
import com.yoke.gainful.database.model.TransactionEntity

@Database(
    entities = [AssetEntity::class, TransactionEntity::class],
    version = 2
)
abstract class GainfulDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("""
                    CREATE TABLE IF NOT EXISTS `transactions_new` (
                        `id` TEXT NOT NULL,
                        `asset_id` TEXT NOT NULL,
                        `type` INTEGER NOT NULL,
                        `quantity` REAL NOT NULL,
                        `price` REAL NOT NULL,
                        `amount` REAL NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                connection.execSQL("""
                    INSERT INTO `transactions_new` (`id`, `asset_id`, `type`, `quantity`, `price`, `amount`, `timestamp`)
                    SELECT `id`, `asset_id`, `type`, `quantity`, `price`,
                        CASE `type`
                            WHEN 0 THEN `price` * `quantity` + `fee`
                            WHEN 1 THEN `price` * `quantity` - `fee`
                            ELSE `fee`
                        END,
                        `timestamp`
                    FROM `transactions`
                """.trimIndent())
                connection.execSQL("DROP TABLE `transactions`")
                connection.execSQL("ALTER TABLE `transactions_new` RENAME TO `transactions`")
            }
        }
    }
}
