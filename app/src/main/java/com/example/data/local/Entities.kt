package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val wallpaperId: String,
    val title: String,
    val category: String,
    val imageUrl: String,
    val thumbnailUrl: String,
    val resolution: String,
    val isAmoled: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads")
data class DownloadedEntity(
    @PrimaryKey
    val wallpaperId: String,
    val title: String,
    val category: String,
    val imageUrl: String,
    val localUri: String,
    val resolution: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_wallpapers")
data class CustomAiEntity(
    @PrimaryKey
    val id: String,
    val prompt: String,
    val style: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_interactions")
data class UserInteractionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wallpaperId: String,
    val category: String,
    val tagsString: String,
    val isAmoled: Boolean,
    val interactionType: String, // "VIEW", "FAVORITE", "DOWNLOAD", "APPLIED"
    val timestamp: Long = System.currentTimeMillis()
)
