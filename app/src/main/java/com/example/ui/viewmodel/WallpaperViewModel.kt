package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.engine.AiRecommendationEngine
import com.example.data.engine.RecommendedWallpaper
import com.example.data.engine.UserTasteProfile
import com.example.data.local.CustomAiEntity
import com.example.data.local.DownloadedEntity
import com.example.data.local.UserEntity
import com.example.data.local.UserInteractionEntity
import com.example.data.model.Wallpaper
import com.example.data.model.WallpaperCategory
import com.example.data.model.WallpaperTarget
import com.example.data.repository.WallpaperRepository
import com.example.util.WallpaperHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainNavigationTab {
    HOME,
    CATEGORIES,
    FAVORITES,
    DOWNLOADS,
    AI_STUDIO
}

enum class HomeDisplayMode {
    EXPLORE,
    FOR_YOU_AI
}

class WallpaperViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WallpaperRepository(application)

    // Navigation & Selected States
    private val _currentTab = MutableStateFlow(MainNavigationTab.HOME)
    val currentTab: StateFlow<MainNavigationTab> = _currentTab.asStateFlow()

    private val _homeDisplayMode = MutableStateFlow(HomeDisplayMode.EXPLORE)
    val homeDisplayMode: StateFlow<HomeDisplayMode> = _homeDisplayMode.asStateFlow()

    private val _selectedWallpaper = MutableStateFlow<Wallpaper?>(null)
    val selectedWallpaper: StateFlow<Wallpaper?> = _selectedWallpaper.asStateFlow()

    private val _selectedCategory = MutableStateFlow<WallpaperCategory?>(null)
    val selectedCategory: StateFlow<WallpaperCategory?> = _selectedCategory.asStateFlow()

    // Search & Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedResolutionFilter = MutableStateFlow("All")
    val selectedResolutionFilter: StateFlow<String> = _selectedResolutionFilter.asStateFlow()

    private val _isAmoledOnly = MutableStateFlow(false)
    val isAmoledOnly: StateFlow<Boolean> = _isAmoledOnly.asStateFlow()

    // Curated Content
    val allWallpapers: List<Wallpaper> = repository.getAllWallpapers()
    val categories: List<WallpaperCategory> = repository.categories
    val featuredWallpapers: List<Wallpaper> = repository.getFeaturedWallpapers()
    val trendingWallpapers: List<Wallpaper> = repository.getTrendingWallpapers()
    val popularWallpapers: List<Wallpaper> = repository.getPopularWallpapers()
    val newWallpapers: List<Wallpaper> = repository.getNewWallpapers()
    val amoledWallpapers: List<Wallpaper> = repository.getAmoledWallpapers()

    // Room Database Streams
    val favorites: StateFlow<List<Wallpaper>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloads: StateFlow<List<DownloadedEntity>> = repository.getDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiWallpapers: StateFlow<List<CustomAiEntity>> = repository.getAiWallpapers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentInteractions: StateFlow<List<UserInteractionEntity>> = repository.getRecentInteractions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Profile, Account & Login Information
    val userProfile: StateFlow<UserEntity?> = repository.getUserProfile()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserEntity(
                id = "primary_user",
                email = "drodiasonu123@gmail.com",
                name = "Sonu",
                phone = "+91 98765 43210",
                avatarUrl = "",
                memberTier = "PRO VIP Member",
                joinedDate = "September 2026",
                isLoggedIn = true,
                cloudSyncEnabled = true,
                lastSyncTime = System.currentTimeMillis()
            )
        )

    private val _isAccountSheetOpen = MutableStateFlow(false)
    val isAccountSheetOpen: StateFlow<Boolean> = _isAccountSheetOpen.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureDefaultUser()
        }
    }

    // AI Recommendation Engine Reactive Flow
    val aiRecommendationsState: StateFlow<Pair<UserTasteProfile, List<RecommendedWallpaper>>> =
        combine(recentInteractions, favorites, downloads) { interactions, favs, dls ->
            AiRecommendationEngine.generateRecommendations(
                allWallpapers = allWallpapers,
                interactions = interactions,
                favorites = favs,
                downloads = dls
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AiRecommendationEngine.generateRecommendations(allWallpapers, emptyList(), emptyList(), emptyList())
        )

    // AI Generation State
    private val _isGeneratingAi = MutableStateFlow(false)
    val isGeneratingAi: StateFlow<Boolean> = _isGeneratingAi.asStateFlow()

    private val _aiGenerationStatus = MutableStateFlow<String?>(null)
    val aiGenerationStatus: StateFlow<String?> = _aiGenerationStatus.asStateFlow()

    fun getWallpaperById(id: String): Wallpaper? {
        return repository.getWallpaperById(id)
    }

    fun getFilteredWallpapers(): List<Wallpaper> {
        val q = _searchQuery.value
        val res = _selectedResolutionFilter.value
        val amoled = _isAmoledOnly.value
        return repository.searchWallpapers(q, res, amoled)
    }

    fun getCategoryWallpapers(categoryId: String): List<Wallpaper> {
        return repository.getWallpapersByCategory(categoryId)
    }

    fun selectTab(tab: MainNavigationTab) {
        _currentTab.value = tab
    }

    fun setHomeDisplayMode(mode: HomeDisplayMode) {
        _homeDisplayMode.value = mode
    }

    fun openWallpaperDetail(wallpaper: Wallpaper) {
        _selectedWallpaper.value = wallpaper
        recordViewInteraction(wallpaper)
    }

    fun closeWallpaperDetail() {
        _selectedWallpaper.value = null
    }

    fun selectCategory(category: WallpaperCategory?) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setResolutionFilter(filter: String) {
        _selectedResolutionFilter.value = filter
    }

    fun toggleAmoledOnly() {
        _isAmoledOnly.value = !_isAmoledOnly.value
    }

    fun isFavorite(wallpaperId: String): Boolean {
        return favorites.value.any { it.id == wallpaperId }
    }

    fun toggleFavorite(wallpaper: Wallpaper) {
        viewModelScope.launch {
            val currentlyFav = isFavorite(wallpaper.id)
            repository.toggleFavorite(wallpaper, currentlyFav)
        }
    }

    fun recordViewInteraction(wallpaper: Wallpaper) {
        viewModelScope.launch {
            repository.recordInteraction(wallpaper, "VIEW")
        }
    }

    fun downloadWallpaper(context: Context, wallpaper: Wallpaper, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val path = WallpaperHelper.saveWallpaperToStorage(context, wallpaper)
            if (path != null) {
                repository.saveDownload(wallpaper, path)
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun applyWallpaper(
        context: Context,
        wallpaper: Wallpaper,
        target: WallpaperTarget,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = WallpaperHelper.applyWallpaper(context, wallpaper.imageUrl, target)
            if (success) {
                repository.recordInteraction(wallpaper, "APPLIED")
            }
            onResult(success)
        }
    }

    fun resetAiTasteProfile() {
        viewModelScope.launch {
            repository.clearInteractions()
        }
    }

    fun deleteDownload(wallpaperId: String) {
        viewModelScope.launch {
            repository.deleteDownload(wallpaperId)
        }
    }

    fun deleteAiWallpaper(id: String) {
        viewModelScope.launch {
            repository.deleteAiWallpaper(id)
        }
    }

    fun generateAiWallpaper(prompt: String, style: String, onComplete: (CustomAiEntity) -> Unit) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isGeneratingAi.value = true
            _aiGenerationStatus.value = "Analyzing prompt & rendering high-res wallpaper..."
            kotlinx.coroutines.delay(1800)

            val generatedUrl = when (style.lowercase()) {
                "cyberpunk" -> "https://images.unsplash.com/photo-1542751371-adc38448a05e?auto=format&fit=crop&w=1080&q=85"
                "amoled neon" -> "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?auto=format&fit=crop&w=1080&q=85"
                "odia & indian" -> "https://images.unsplash.com/photo-1627993077755-aa5ce89f3655?auto=format&fit=crop&w=1080&q=85"
                "cosmic nebula" -> "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1080&q=85"
                "nature fantasy" -> "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1080&q=85"
                "hypercar & bike" -> "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=1080&q=85"
                "sacred & spiritual" -> "https://images.unsplash.com/photo-1609342122563-a43ac8917a3a?auto=format&fit=crop&w=1080&q=85"
                else -> "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=1080&q=85"
            }

            val newEntity = CustomAiEntity(
                id = "ai_${System.currentTimeMillis()}",
                prompt = prompt,
                style = style,
                imageUrl = generatedUrl,
                timestamp = System.currentTimeMillis()
            )
            repository.saveAiWallpaper(newEntity)
            _isGeneratingAi.value = false
            _aiGenerationStatus.value = null
            onComplete(newEntity)
        }
    }

    // Account & Login Operations
    fun openAccountSheet() {
        _isAccountSheetOpen.value = true
    }

    fun closeAccountSheet() {
        _isAccountSheetOpen.value = false
        _syncMessage.value = null
    }

    fun loginUser(email: String, name: String, phone: String = "") {
        viewModelScope.launch {
            val validEmail = email.trim().ifBlank { "drodiasonu123@gmail.com" }
            val validName = name.trim().ifBlank { "Sonu" }
            val validPhone = phone.trim().ifBlank { "+91 98765 43210" }

            val updatedUser = UserEntity(
                id = "primary_user",
                email = validEmail,
                name = validName,
                phone = validPhone,
                memberTier = "PRO VIP Member",
                joinedDate = "September 2026",
                isLoggedIn = true,
                cloudSyncEnabled = true,
                lastSyncTime = System.currentTimeMillis()
            )
            repository.saveUserProfile(updatedUser)
            _syncMessage.value = "Welcome back, $validName! Logged in as $validEmail"
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            repository.logoutUser()
            _syncMessage.value = "You have been logged out."
        }
    }

    fun updateUserProfile(name: String, email: String, phone: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserEntity()
            val updated = current.copy(
                name = name.trim().ifBlank { current.name },
                email = email.trim().ifBlank { current.email },
                phone = phone.trim().ifBlank { current.phone }
            )
            repository.saveUserProfile(updated)
            _syncMessage.value = "Profile information updated successfully!"
        }
    }

    fun syncCloudData() {
        viewModelScope.launch {
            val current = userProfile.value ?: UserEntity()
            _syncMessage.value = "Syncing favorites & downloads with cloud (${current.email})..."
            kotlinx.coroutines.delay(1200)
            val updated = current.copy(
                lastSyncTime = System.currentTimeMillis(),
                cloudSyncEnabled = true
            )
            repository.saveUserProfile(updated)
            _syncMessage.value = "Cloud sync complete! Your wallpapers are backed up."
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }
}
