package com.yoke.gainful.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<GainfulDatabase> {
    val dbFile = context.getDatabasePath(DATABASE_NAME)
    return Room.databaseBuilder<GainfulDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}
