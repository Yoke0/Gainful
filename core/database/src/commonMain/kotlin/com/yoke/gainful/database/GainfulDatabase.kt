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
import com.yoke.gainful.database.dao.KLineCacheDao
import com.yoke.gainful.database.dao.PnlCacheDao
import com.yoke.gainful.database.dao.QuoteSnapshotDao
import com.yoke.gainful.database.dao.SyncQueueDao
import com.yoke.gainful.database.dao.TransactionDao
import com.yoke.gainful.database.model.AssetEntity
import com.yoke.gainful.database.model.KLineCacheEntity
import com.yoke.gainful.database.model.PnlCacheEntity
import com.yoke.gainful.database.model.QuoteSnapshotEntity
import com.yoke.gainful.database.model.SyncQueueEntity
import com.yoke.gainful.database.model.TransactionEntity

@Database(
    entities = [
        AssetEntity::class,
        TransactionEntity::class,
        QuoteSnapshotEntity::class,
        KLineCacheEntity::class,
        PnlCacheEntity::class,
        SyncQueueEntity::class,
    ],
    version = 7,
)
@TypeConverters(Converters::class)
@ConstructedBy(GainfulDatabaseConstructor::class)
abstract class GainfulDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao

    abstract fun transactionDao(): TransactionDao

    abstract fun syncQueueDao(): SyncQueueDao

    abstract fun quoteSnapshotDao(): QuoteSnapshotDao

    abstract fun kLineCacheDao(): KLineCacheDao

    abstract fun pnlCacheDao(): PnlCacheDao

    companion object {
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(connection: SQLiteConnection) {
                    connection.execSQL(
                        """
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
                        """.trimIndent(),
                    )
                    connection.execSQL(
                        """
                        INSERT INTO `transactions_new` (`id`, `asset_id`, `type`, `quantity`, `price`, `amount`, `timestamp`)
                        SELECT `id`, `asset_id`, `type`, `quantity`, `price`,
                            CASE `type`
                                WHEN 0 THEN `price` * `quantity` + `fee`
                                WHEN 1 THEN `price` * `quantity` - `fee`
                                ELSE `fee`
                            END,
                            `timestamp`
                        FROM `transactions`
                        """.trimIndent(),
                    )
                    connection.execSQL("DROP TABLE `transactions`")
                    connection.execSQL("ALTER TABLE `transactions_new` RENAME TO `transactions`")
                }
            }

        val MIGRATION_2_3 =
            object : Migration(2, 3) {
                override fun migrate(connection: SQLiteConnection) {
                    connection.execSQL("ALTER TABLE `transactions` ADD COLUMN `trade_date` INTEGER NOT NULL DEFAULT 0")
                    connection.execSQL("UPDATE `transactions` SET `trade_date` = `timestamp` WHERE `trade_date` = 0")
                }
            }

        val MIGRATION_3_4 =
            object : Migration(3, 4) {
                override fun migrate(connection: SQLiteConnection) {
                    connection.execSQL(
                        """
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
                        """.trimIndent(),
                    )
                }
            }

        val MIGRATION_4_5 =
            object : Migration(4, 5) {
                override fun migrate(connection: SQLiteConnection) {
                    connection.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `kline_cache` (
                            `asset_id` TEXT NOT NULL,
                            `date` TEXT NOT NULL,
                            `open` REAL NOT NULL,
                            `close` REAL NOT NULL,
                            `high` REAL NOT NULL,
                            `low` REAL NOT NULL,
                            `volume` INTEGER NOT NULL,
                            `turnover` REAL NOT NULL,
                            `change_percent` REAL NOT NULL,
                            `change_amount` REAL NOT NULL,
                            `last_updated` INTEGER NOT NULL,
                            PRIMARY KEY(`asset_id`, `date`)
                        )
                        """.trimIndent(),
                    )
                }
            }

        val MIGRATION_5_6 =
            object : Migration(5, 6) {
                override fun migrate(connection: SQLiteConnection) {
                    connection.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `pnl_cache` (
                            `date` TEXT NOT NULL,
                            `pnl` REAL NOT NULL,
                            `last_updated` INTEGER NOT NULL,
                            PRIMARY KEY(`date`)
                        )
                        """.trimIndent(),
                    )
                }
            }

        val MIGRATION_6_7 =
            object : Migration(6, 7) {
                override fun migrate(connection: SQLiteConnection) {
                    connection.execSQL("ALTER TABLE `transactions` ADD COLUMN `updated_at` INTEGER NOT NULL DEFAULT 0")
                    connection.execSQL("UPDATE `transactions` SET `updated_at` = `timestamp` WHERE `updated_at` = 0")
                    connection.execSQL(
                        "CREATE TABLE IF NOT EXISTS `sync_queue` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `entity_type` TEXT NOT NULL, `entity_id` TEXT NOT NULL, `operation` TEXT NOT NULL, `created_at` INTEGER NOT NULL)",
                    )
                }
            }
    }
}

expect object GainfulDatabaseConstructor : RoomDatabaseConstructor<GainfulDatabase> {
    override fun initialize(): GainfulDatabase
}
