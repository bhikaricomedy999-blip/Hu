package com.example.data.model

data class Wallpaper(
    val id: String,
    val title: String,
    val category: String,
    val imageUrl: String,
    val thumbnailUrl: String = imageUrl,
    val resolution: String = "4K UHD",
    val isAmoled: Boolean = false,
    val downloadsCount: Int = 1200,
    val viewsCount: Int = 5400,
    val tags: List<String> = emptyList(),
    val colorHex: String = "#00F0FF",
    val authorName: String = "Wallpaper World Studio",
    val authorProfile: String = "@wallpaperworld",
    val description: String = "",
    val isTrending: Boolean = false,
    val isPopular: Boolean = false,
    val isFeatured: Boolean = false,
    val isNew: Boolean = false
)

enum class WallpaperTarget {
    HOME_SCREEN,
    LOCK_SCREEN,
    BOTH
}

data class WallpaperCategory(
    val id: String,
    val name: String,
    val odiaName: String,
    val iconName: String,
    val coverImageUrl: String,
    val wallpaperCount: Int,
    val description: String
)
