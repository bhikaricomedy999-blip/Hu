package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WallpaperDao {

    // Favorites
    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE wallpaperId = :id)")
    fun isFavorite(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE wallpaperId = :id")
    suspend fun deleteFavorite(id: String)

    // Downloads
    @Query("SELECT * FROM downloads ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<DownloadedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadedEntity)

    @Query("DELETE FROM downloads WHERE wallpaperId = :id")
    suspend fun deleteDownload(id: String)

    // AI Wallpapers
    @Query("SELECT * FROM ai_wallpapers ORDER BY timestamp DESC")
    fun getAllAiWallpapers(): Flow<List<CustomAiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiWallpaper(aiWallpaper: CustomAiEntity)

    @Query("DELETE FROM ai_wallpapers WHERE id = :id")
    suspend fun deleteAiWallpaper(id: String)

    // User Interactions for AI Recommendation Engine
    @Insert
    suspend fun insertInteraction(interaction: UserInteractionEntity)

    @Query("SELECT * FROM user_interactions ORDER BY timestamp DESC LIMIT 250")
    fun getRecentInteractions(): Flow<List<UserInteractionEntity>>

    @Query("DELETE FROM user_interactions")
    suspend fun clearAllInteractions()

    // User Profile & Login
    @Query("SELECT * FROM user_profile WHERE id = 'primary_user' LIMIT 1")
    fun getUserProfile(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(user: UserEntity)

    @Query("UPDATE user_profile SET isLoggedIn = 0 WHERE id = 'primary_user'")
    suspend fun logoutUser()
}
