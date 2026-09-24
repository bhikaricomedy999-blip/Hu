package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Wallpaper World", appName)
  }

  @Test
  fun `verify categories and wallpapers loaded`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.repository.WallpaperRepository(context)
    val categories = repo.categories
    val wallpapers = repo.getAllWallpapers()
    org.junit.Assert.assertTrue(categories.isNotEmpty())
    org.junit.Assert.assertTrue(wallpapers.isNotEmpty())
    org.junit.Assert.assertNotNull(repo.getFeaturedWallpapers())
  }
}
