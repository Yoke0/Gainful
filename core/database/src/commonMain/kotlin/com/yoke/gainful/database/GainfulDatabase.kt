package com.yoke.gainful.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.yoke.gainful.database.dao.AssetDao
import com.yoke.gainful.database.dao.QuoteSnapshotDao
import com.yoke.gainful.database.dao.TransactionDao
import com.yoke.gainful.database.model.AssetEntity
import com.yoke.gainful.database.model.QuoteSnapshotEntity
import com.yoke.gainful.database.model.TransactionEntity

@Database(
    entities = [AssetEntity::class, TransactionEntity::class, QuoteSnapshotEntity::class],
    version = 4
)
@TypeConverters(Converters::class)
@ConstructedBy(GainfulDatabaseConstructor::class)
abstract class GainfulDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun transactionDao(): TransactionDao
    abstract fun quoteSnapshotDao(): QuoteSnapshotDao

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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE `transactions` ADD COLUMN `trade_date` INTEGER NOT NULL DEFAULT 0")
                connection.execSQL("UPDATE `transactions` SET `trade_date` = `timestamp` WHERE `trade_date` = 0")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("""
                    CREATE TABLE IF NOT EXISTS `quote_snapshots` (
                        `quote_id` TEXT NOT NULL,
                        `code` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `latest_price` REAL NOT NULL,
                        `change_percent` REAL NOT NULL,
                        `change_amount` REAL NOT NULL,
                        `high` REAL NOT NULL,
                        `low` REAL NOT NULL,
                        `open` REAL NOT NULL,
                        `pre_close` REAL NOT NULL,
                        `volume` INTEGER NOT NULL,
                        `turnover` REAL NOT NULL,
                        `amplitude` REAL NOT NULL,
                        `turnover_rate` REAL NOT NULL,
                        `pe_dynamic` REAL NOT NULL,
                        `total_market_cap` REAL NOT NULL,
                        `circulating_market_cap` REAL NOT NULL,
                        `pb` REAL NOT NULL,
                        `industry` TEXT NOT NULL,
                        `trend_data` TEXT NOT NULL,
                        `last_updated` INTEGER NOT NULL,
                        PRIMARY KEY(`quote_id`)
                    )
                """.trimIndent())
            }
        }
    }
}

expect object GainfulDatabaseConstructor : RoomDatabaseConstructor<GainfulDatabase> {
    override fun initialize(): GainfulDatabase
}
