package com.yoke.gainful.database

import androidx.room.RoomDatabase

const val DATABASE_NAME = "gainful.db"

fun createDatabase(builder: RoomDatabase.Builder<GainfulDatabase>): GainfulDatabase {
    return builder
        .addMigrations(
            GainfulDatabase.MIGRATION_1_2,
            GainfulDatabase.MIGRATION_2_3,
            GainfulDatabase.MIGRATION_3_4,
            GainfulDatabase.MIGRATION_4_5,
        )
        .build()
}
