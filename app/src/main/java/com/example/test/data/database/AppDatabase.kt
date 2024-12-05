package com.example.test.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.test.data.helper.FavoriteEvent

@Database(entities = [FavoriteEvent::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteEventDao(): FavoriteEventDao
}
