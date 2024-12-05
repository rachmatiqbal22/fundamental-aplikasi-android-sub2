package com.example.test.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.test.data.helper.FavoriteEvent

@Dao
interface FavoriteEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteEvent: FavoriteEvent)

    @Delete
    suspend fun delete(favoriteEvent: FavoriteEvent)

    @Query("SELECT * FROM favorite_event WHERE id = :id")
    fun getFavoriteEventById(id: Int): LiveData<FavoriteEvent>

    @Query("SELECT * FROM favorite_event WHERE id = :id")
    fun isExistFavoriteEventById(id: Int): Boolean

    @Query("SELECT * FROM favorite_event")
    fun getAllFavorites(): LiveData<List<FavoriteEvent>>
}

