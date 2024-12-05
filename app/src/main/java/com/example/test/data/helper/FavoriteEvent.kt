package com.example.test.data.helper

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_event")
data class FavoriteEvent(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String?,
    val organizer: String?,
    val quota: Int,
    val registrants: Int,
    val mediaCover: String,
    val link: String,
    val fullDescription: String?

)
