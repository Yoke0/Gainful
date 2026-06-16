package com.yoke.gainful.database

import androidx.room.RoomDatabase

const val DATABASE_NAME = "gainful.db"

fun createDatabase(builder: RoomDatabase.Builder<GainfulDatabase>): GainfulDatabase {
    return builder.build()
}
