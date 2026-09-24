package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.DownloadedEntity
import com.example.data.model.Wallpaper
import com.example.data.model.WallpaperTarget
import com.example.ui.components.SetWallpaperSheet
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainNavigationTab
import com.example.ui.viewmodel.WallpaperViewModel
import java.io.File

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun DownloadsScreen(
    viewModel: WallpaperViewModel,
    onWallpaperSelected: (Wallpaper) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val downloads by viewModel.downloads.collectAsState()

    var selectedForSet by remember { mutableStateOf<DownloadedEntity?>(null) }
    var isApplying by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AmoledBackground)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = "Downloads",
                    tint = CyanNeon,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Downloads & Offline",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "${downloads.size} wallpapers saved on your device for offline viewing & application",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        if (downloads.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.OfflinePin,
                        contentDescription = "No Downloads",
                        tint = CyanNeon.copy(alpha = 0.4f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Downloads Yet",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap the download button on any wallpaper to save it locally for offline use.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.selectTab(MainNavigationTab.HOME) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanNeon,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("downloads_explore_btn")
                    ) {
                        Text(text = "Find Wallpapers", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(downloads) { item ->
                    DownloadedWallpaperCard(
                        download = item,
                        onClick = {
                            val wp = viewModel.getWallpaperById(item.wallpaperId) ?: Wallpaper(
                                id = item.wallpaperId,
                                title = item.title,
                                category = item.category,
                                imageUrl = if (File(item.localUri).exists()) item.localUri else item.imageUrl,
                                resolution = item.resolution
                            )
                            onWallpaperSelected(wp)
                        },
                        onApplyClick = { selectedForSet = item },
                        onDeleteClick = { viewModel.deleteDownload(item.wallpaperId) }
                    )
                }
            }
        }

        // Apply Wallpaper Sheet
        if (selectedForSet != null) {
            val currentItem = selectedForSet!!
            SetWallpaperSheet(
                isApplying = isApplying,
                onTargetSelected = { target ->
                    isApplying = true
                    val wp = Wallpaper(
                        id = currentItem.wallpaperId,
                        title = currentItem.title,
                        category = currentItem.category,
                        imageUrl = if (File(currentItem.localUri).exists()) currentItem.localUri else currentItem.imageUrl,
                        resolution = currentItem.resolution
                    )
                    viewModel.applyWallpaper(context, wp, target) { success ->
                        isApplying = false
                        selectedForSet = null
                        val msg = if (success) "Successfully applied wallpaper from offline storage!" else "Could not apply wallpaper"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                },
                onDismiss = {
                    if (!isApplying) selectedForSet = null
                }
            )
        }
    }
}

@Composable
fun DownloadedWallpaperCard(
    download: DownloadedEntity,
    onClick: () -> Unit,
    onApplyClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.68f)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("download_card_${download.wallpaperId}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val localFile = File(download.localUri)
            val model = if (localFile.exists()) localFile else download.imageUrl

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(model)
                    .crossfade(true)
                    .build(),
                contentDescription = download.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Top Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = EmeraldNeon.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.OfflinePin,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Offline",
                            color = Color.Black,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Bottom Actions & Title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f),
                                Color.Black.copy(alpha = 0.95f)
                            )
                        )
                    )
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = download.title,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onApplyClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanNeon,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Wallpaper,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Apply Now",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
