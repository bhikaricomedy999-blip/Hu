package com.example.util

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.Toast
import com.example.data.model.Wallpaper
import com.example.data.model.WallpaperTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object WallpaperHelper {

    suspend fun downloadBitmap(imageUrl: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun applyWallpaper(
        context: Context,
        imageUrl: String,
        target: WallpaperTarget
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val bitmap = downloadBitmap(imageUrl) ?: return@withContext false
            val wallpaperManager = WallpaperManager.getInstance(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val whichFlag = when (target) {
                    WallpaperTarget.HOME_SCREEN -> WallpaperManager.FLAG_SYSTEM
                    WallpaperTarget.LOCK_SCREEN -> WallpaperManager.FLAG_LOCK
                    WallpaperTarget.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                }
                wallpaperManager.setBitmap(bitmap, null, true, whichFlag)
            } else {
                wallpaperManager.setBitmap(bitmap)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun saveWallpaperToStorage(context: Context, wallpaper: Wallpaper): String? =
        withContext(Dispatchers.IO) {
            try {
                val bitmap = downloadBitmap(wallpaper.imageUrl) ?: return@withContext null
                val filename = "wp_${wallpaper.id}_${System.currentTimeMillis()}.jpg"
                val dir = File(context.getExternalFilesDir(null), "Wallpapers")
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val file = File(dir, filename)
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                out.flush()
                out.close()
                file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    fun shareWallpaper(context: Context, wallpaper: Wallpaper) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_SUBJECT,
                "Check out this ${wallpaper.resolution} wallpaper: ${wallpaper.title}"
            )
            putExtra(
                Intent.EXTRA_TEXT,
                "📱 Beautiful ${wallpaper.title} on Wallpaper World!\nCategory: ${wallpaper.category.replaceFirstChar { it.uppercase() }}\nResolution: ${wallpaper.resolution}\nDownload & set here: ${wallpaper.imageUrl}"
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Wallpaper via"))
    }
}
