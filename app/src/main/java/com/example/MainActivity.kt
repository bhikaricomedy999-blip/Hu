package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.components.WallpaperBottomBar
import com.example.ui.screens.AiStudioScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CategoryDetailScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PreviewScreen
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.WallpaperWorldTheme
import com.example.ui.viewmodel.MainNavigationTab
import com.example.ui.viewmodel.WallpaperViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WallpaperViewModel by viewModels()

    @androidx.compose.material3.ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WallpaperWorldTheme {
                WallpaperWorldApp(viewModel = viewModel)
            }
        }
    }
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun WallpaperWorldApp(viewModel: WallpaperViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedWallpaper by viewModel.selectedWallpaper.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val downloads by viewModel.downloads.collectAsState()

    // Handle back button presses gracefully
    BackHandler(enabled = selectedWallpaper != null || selectedCategory != null || currentTab != MainNavigationTab.HOME) {
        when {
            selectedWallpaper != null -> viewModel.closeWallpaperDetail()
            selectedCategory != null -> viewModel.selectCategory(null)
            currentTab != MainNavigationTab.HOME -> viewModel.selectTab(MainNavigationTab.HOME)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBackground)
    ) {
        // If a wallpaper is currently opened for preview, show full screen edge-to-edge preview
        if (selectedWallpaper != null) {
            PreviewScreen(
                wallpaper = selectedWallpaper!!,
                viewModel = viewModel,
                onBack = { viewModel.closeWallpaperDetail() }
            )
        } else if (selectedCategory != null) {
            // Category detail screen
            CategoryDetailScreen(
                category = selectedCategory!!,
                viewModel = viewModel,
                onBack = { viewModel.selectCategory(null) },
                onWallpaperSelected = { wp -> viewModel.openWallpaperDetail(wp) }
            )
        } else {
            // Main scaffold with bottom navigation
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("main_scaffold"),
                containerColor = AmoledBackground,
                bottomBar = {
                    WallpaperBottomBar(
                        currentTab = currentTab,
                        onTabSelected = { tab ->
                            viewModel.selectTab(tab)
                        },
                        favoritesCount = favorites.size,
                        downloadsCount = downloads.size
                    )
                }
            ) { innerPadding ->
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tabTransition",
                    modifier = Modifier.padding(innerPadding)
                ) { tab ->
                    when (tab) {
                        MainNavigationTab.HOME -> {
                            HomeScreen(
                                viewModel = viewModel,
                                onCategorySelected = { cat -> viewModel.selectCategory(cat) },
                                onWallpaperSelected = { wp -> viewModel.openWallpaperDetail(wp) }
                            )
                        }
                        MainNavigationTab.CATEGORIES -> {
                            CategoriesScreen(
                                viewModel = viewModel,
                                onCategoryClick = { cat -> viewModel.selectCategory(cat) }
                            )
                        }
                        MainNavigationTab.AI_STUDIO -> {
                            AiStudioScreen(
                                viewModel = viewModel,
                                onWallpaperSelected = { wp -> viewModel.openWallpaperDetail(wp) }
                            )
                        }
                        MainNavigationTab.FAVORITES -> {
                            FavoritesScreen(
                                viewModel = viewModel,
                                onWallpaperSelected = { wp -> viewModel.openWallpaperDetail(wp) }
                            )
                        }
                        MainNavigationTab.DOWNLOADS -> {
                            DownloadsScreen(
                                viewModel = viewModel,
                                onWallpaperSelected = { wp -> viewModel.openWallpaperDetail(wp) }
                            )
                        }
                    }
                }
            }
        }
    }
}
