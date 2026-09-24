package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Wallpaper
import com.example.data.model.WallpaperTarget
import com.example.ui.components.SetWallpaperSheet
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.WallpaperHelper
import com.example.ui.viewmodel.WallpaperViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class PreviewSimulationMode {
    NONE,
    LOCK_SCREEN,
    HOME_SCREEN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    wallpaper: Wallpaper,
    viewModel: WallpaperViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val favorites by viewModel.favorites.collectAsState()
    val isFav = favorites.any { it.id == wallpaper.id }

    var isUiVisible by remember { mutableStateOf(true) }
    var simulationMode by remember { mutableStateOf(PreviewSimulationMode.NONE) }
    var showSetWallpaperSheet by remember { mutableStateOf(false) }
    var isApplyingWallpaper by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    val heartScale by animateFloatAsState(
        targetValue = if (isFav) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heartScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isUiVisible = !isUiVisible
                    }
                )
            }
    ) {
        // High Quality Fullscreen Wallpaper Image
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(wallpaper.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = wallpaper.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay: Simulation Mode (Lock Screen / Home Screen)
        when (simulationMode) {
            PreviewSimulationMode.LOCK_SCREEN -> {
                LockScreenSimulatorOverlay(wallpaper = wallpaper)
            }
            PreviewSimulationMode.HOME_SCREEN -> {
                HomeScreenSimulatorOverlay(wallpaper = wallpaper)
            }
            PreviewSimulationMode.NONE -> {
                // Regular clean preview
            }
        }

        // Top Bar (Animated Visibility)
        AnimatedVisibility(
            visible = isUiVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.size(42.dp)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("preview_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }

                    // Simulation Mode Switcher Chips
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            SimulationChip(
                                text = "Standard",
                                isSelected = simulationMode == PreviewSimulationMode.NONE,
                                onClick = { simulationMode = PreviewSimulationMode.NONE }
                            )
                            SimulationChip(
                                text = "Lock Screen",
                                isSelected = simulationMode == PreviewSimulationMode.LOCK_SCREEN,
                                onClick = { simulationMode = PreviewSimulationMode.LOCK_SCREEN }
                            )
                            SimulationChip(
                                text = "Home Apps",
                                isSelected = simulationMode == PreviewSimulationMode.HOME_SCREEN,
                                onClick = { simulationMode = PreviewSimulationMode.HOME_SCREEN }
                            )
                        }
                    }

                    // Info Button
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.size(42.dp)
                    ) {
                        IconButton(
                            onClick = { showInfoDialog = true },
                            modifier = Modifier.testTag("preview_info_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Info",
                                tint = CyanNeon
                            )
                        }
                    }
                }
            }
        }

        // Bottom Glassmorphic Actions Dock (Animated Visibility)
        AnimatedVisibility(
            visible = isUiVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f),
                                Color.Black.copy(alpha = 0.98f)
                            )
                        )
                    )
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Wallpaper Title & Specs
                Text(
                    text = wallpaper.title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${wallpaper.category.replaceFirstChar { it.uppercase() }} • ${wallpaper.resolution} • ${if (wallpaper.isAmoled) "True AMOLED" else "Vibrant HDR"}",
                    color = CyanNeon,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Action Controls Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Favorite Toggle Button
                    Surface(
                        color = DarkSurfaceElevated.copy(alpha = 0.9f),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.size(52.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleFavorite(wallpaper) },
                            modifier = Modifier.testTag("preview_favorite_toggle")
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFav) PinkNeon else Color.White,
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(heartScale)
                            )
                        }
                    }

                    // Main "Set Wallpaper" Primary Action Button
                    Button(
                        onClick = { showSetWallpaperSheet = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .padding(horizontal = 12.dp)
                            .testTag("set_wallpaper_main_button"),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanNeon,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Wallpaper,
                            contentDescription = "Apply",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Set Wallpaper",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Download Action Button
                    Surface(
                        color = DarkSurfaceElevated.copy(alpha = 0.9f),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.size(52.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (!isDownloading) {
                                    isDownloading = true
                                    viewModel.downloadWallpaper(context, wallpaper) { success ->
                                        isDownloading = false
                                        val message = if (success) "Wallpaper saved to Downloads!" else "Download failed. Please check network."
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.testTag("preview_download_button")
                        ) {
                            if (isDownloading) {
                                CircularProgressIndicator(
                                    color = CyanNeon,
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Download,
                                    contentDescription = "Download",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Share Button
                    Surface(
                        color = DarkSurfaceElevated.copy(alpha = 0.9f),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.size(52.dp)
                    ) {
                        IconButton(
                            onClick = {
                                com.example.util.WallpaperHelper.shareWallpaper(context, wallpaper)
                            },
                            modifier = Modifier.testTag("preview_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap screen anytime to toggle UI controls",
                    color = TextSecondary.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        }

        // Set Wallpaper BottomSheet
        if (showSetWallpaperSheet) {
            SetWallpaperSheet(
                isApplying = isApplyingWallpaper,
                onTargetSelected = { target ->
                    isApplyingWallpaper = true
                    viewModel.applyWallpaper(context, wallpaper, target) { success ->
                        isApplyingWallpaper = false
                        showSetWallpaperSheet = false
                        val targetName = when (target) {
                            WallpaperTarget.HOME_SCREEN -> "Home Screen"
                            WallpaperTarget.LOCK_SCREEN -> "Lock Screen"
                            WallpaperTarget.BOTH -> "Home & Lock Screen"
                        }
                        val msg = if (success) "Successfully applied to $targetName!" else "Could not apply wallpaper"
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }
                },
                onDismiss = {
                    if (!isApplyingWallpaper) {
                        showSetWallpaperSheet = false
                    }
                }
            )
        }

        // Info Dialog
        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                containerColor = DarkSurface,
                title = {
                    Text(
                        text = wallpaper.title,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = wallpaper.description.ifEmpty { "High resolution wallpaper optimized for modern AMOLED and Retina smartphone displays." },
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        InfoRow(label = "Category", value = wallpaper.category.replaceFirstChar { it.uppercase() })
                        InfoRow(label = "Resolution", value = wallpaper.resolution)
                        InfoRow(label = "AMOLED Optimized", value = if (wallpaper.isAmoled) "Yes (True Black)" else "No")
                        InfoRow(label = "Downloads", value = "${wallpaper.downloadsCount}+")
                        InfoRow(label = "Views", value = "${wallpaper.viewsCount}+")
                        InfoRow(label = "Creator", value = wallpaper.authorName)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showInfoDialog = false }) {
                        Text("Close", color = CyanNeon, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(text = value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SimulationChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) CyanNeon else Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Black else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun LockScreenSimulatorOverlay(wallpaper: Wallpaper) {
    val currentTime = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
    val currentDate = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Locked",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = currentTime,
                color = Color.White,
                fontSize = 72.sp,
                fontWeight = FontWeight.Thin,
                letterSpacing = (-2).sp
            )
            Text(
                text = currentDate,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "Swipe up to unlock",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 13.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
        )
    }
}

@Composable
private fun HomeScreenSimulatorOverlay(wallpaper: Wallpaper) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Mock App Grid
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp, start = 24.dp, end = 24.dp)
        ) {
            // Mock Search Pill
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Search apps, web...",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Mock Bottom Dock Icons
        Surface(
            color = Color.Black.copy(alpha = 0.45f),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth()
                .height(68.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MockDockIcon(Icons.Filled.Call, CyanNeon)
                MockDockIcon(Icons.Filled.Chat, EmeraldNeon)
                MockDockIcon(Icons.Filled.Language, PurpleNeon)
                MockDockIcon(Icons.Filled.CameraAlt, PinkNeon)
            }
        }
    }
}

@Composable
private fun MockDockIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Surface(
        color = color.copy(alpha = 0.85f),
        shape = CircleShape,
        modifier = Modifier.size(46.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
