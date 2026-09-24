package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainNavigationTab

@Composable
fun WallpaperBottomBar(
    currentTab: MainNavigationTab,
    onTabSelected: (MainNavigationTab) -> Unit,
    favoritesCount: Int,
    downloadsCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = DarkBorder,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        color = DarkSurface.copy(alpha = 0.96f),
        tonalElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(72.dp)
        ) {
            // Home Tab
            NavigationBarItem(
                selected = currentTab == MainNavigationTab.HOME,
                onClick = { onTabSelected(MainNavigationTab.HOME) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == MainNavigationTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = "Home",
                        fontSize = 11.sp,
                        fontWeight = if (currentTab == MainNavigationTab.HOME) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyanNeon,
                    selectedTextColor = CyanNeon,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = CyanNeon.copy(alpha = 0.16f)
                ),
                modifier = Modifier.testTag("nav_home")
            )

            // Categories Tab
            NavigationBarItem(
                selected = currentTab == MainNavigationTab.CATEGORIES,
                onClick = { onTabSelected(MainNavigationTab.CATEGORIES) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == MainNavigationTab.CATEGORIES) Icons.Filled.GridView else Icons.Outlined.GridView,
                        contentDescription = "Categories",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = "Categories",
                        fontSize = 11.sp,
                        fontWeight = if (currentTab == MainNavigationTab.CATEGORIES) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyanNeon,
                    selectedTextColor = CyanNeon,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = CyanNeon.copy(alpha = 0.16f)
                ),
                modifier = Modifier.testTag("nav_categories")
            )

            // AI Studio Tab (Special Highlight)
            NavigationBarItem(
                selected = currentTab == MainNavigationTab.AI_STUDIO,
                onClick = { onTabSelected(MainNavigationTab.AI_STUDIO) },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(PurpleNeon, CyanNeon)
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "AI Studio",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = "AI Studio",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentTab == MainNavigationTab.AI_STUDIO) PurpleNeon else TextSecondary
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PurpleNeon,
                    selectedTextColor = PurpleNeon,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = PurpleNeon.copy(alpha = 0.16f)
                ),
                modifier = Modifier.testTag("nav_ai_studio")
            )

            // Favorites Tab
            NavigationBarItem(
                selected = currentTab == MainNavigationTab.FAVORITES,
                onClick = { onTabSelected(MainNavigationTab.FAVORITES) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (favoritesCount > 0) {
                                Badge(
                                    containerColor = PinkNeon,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = favoritesCount.toString(),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (currentTab == MainNavigationTab.FAVORITES) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorites",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = "Favorites",
                        fontSize = 11.sp,
                        fontWeight = if (currentTab == MainNavigationTab.FAVORITES) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PinkNeon,
                    selectedTextColor = PinkNeon,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = PinkNeon.copy(alpha = 0.16f)
                ),
                modifier = Modifier.testTag("nav_favorites")
            )

            // Downloads Tab
            NavigationBarItem(
                selected = currentTab == MainNavigationTab.DOWNLOADS,
                onClick = { onTabSelected(MainNavigationTab.DOWNLOADS) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (downloadsCount > 0) {
                                Badge(
                                    containerColor = CyanNeon,
                                    contentColor = AmoledBackground
                                ) {
                                    Text(
                                        text = downloadsCount.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (currentTab == MainNavigationTab.DOWNLOADS) Icons.Filled.Download else Icons.Outlined.Download,
                            contentDescription = "Downloads",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = "Downloads",
                        fontSize = 11.sp,
                        fontWeight = if (currentTab == MainNavigationTab.DOWNLOADS) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyanNeon,
                    selectedTextColor = CyanNeon,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = CyanNeon.copy(alpha = 0.16f)
                ),
                modifier = Modifier.testTag("nav_downloads")
            )
        }
    }
}
