package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Wallpaper
import com.example.data.model.WallpaperCategory
import com.example.ui.components.WallpaperCard
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.WallpaperViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    category: WallpaperCategory,
    viewModel: WallpaperViewModel,
    onBack: () -> Unit,
    onWallpaperSelected: (Wallpaper) -> Unit,
    modifier: Modifier = Modifier
) {
    val wallpapers = viewModel.getCategoryWallpapers(category.id)
    val favorites by viewModel.favorites.collectAsState()

    var resolutionFilter by remember { mutableStateOf("All") }
    var amoledOnly by remember { mutableStateOf(false) }

    val filtered = wallpapers.filter { wp ->
        val matchesRes = resolutionFilter == "All" || wp.resolution.contains(resolutionFilter, ignoreCase = true)
        val matchesAmoled = !amoledOnly || wp.isAmoled
        matchesRes && matchesAmoled
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AmoledBackground)
    ) {
        // Hero Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(category.coverImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = category.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Black.copy(alpha = 0.75f),
                                AmoledBackground
                            )
                        )
                    )
            )

            // Top Bar with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("category_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = category.name,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = category.odiaName + " • " + "${category.wallpaperCount}+ Wallpapers",
                        color = CyanNeon,
                        fontSize = 12.sp
                    )
                }
            }

            // Description
            Text(
                text = category.description,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "4K UHD", "QHD+").forEach { res ->
                val isSelected = resolutionFilter == res
                FilterChip(
                    selected = isSelected,
                    onClick = { resolutionFilter = res },
                    label = { Text(text = res, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanNeon.copy(alpha = 0.2f),
                        selectedLabelColor = CyanNeon,
                        containerColor = DarkSurfaceElevated,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) CyanNeon else DarkBorder
                    )
                )
            }

            FilterChip(
                selected = amoledOnly,
                onClick = { amoledOnly = !amoledOnly },
                label = { Text(text = "AMOLED", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PurpleNeon.copy(alpha = 0.2f),
                    selectedLabelColor = PurpleNeon,
                    containerColor = DarkSurfaceElevated,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = amoledOnly,
                    borderColor = if (amoledOnly) PurpleNeon else DarkBorder
                )
            )
        }

        // Wallpapers 2-Column Grid
        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No wallpapers found for current filter",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { wp ->
                    val isFav = favorites.any { it.id == wp.id }
                    WallpaperCard(
                        wallpaper = wp,
                        isFavorite = isFav,
                        onCardClick = { onWallpaperSelected(wp) },
                        onFavoriteToggle = { viewModel.toggleFavorite(wp) }
                    )
                }
            }
        }
    }
}
