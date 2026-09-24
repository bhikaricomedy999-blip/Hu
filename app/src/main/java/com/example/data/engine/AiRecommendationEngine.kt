package com.example.data.engine

import com.example.data.local.DownloadedEntity
import com.example.data.local.UserInteractionEntity
import com.example.data.model.Wallpaper
import kotlin.math.exp
import kotlin.math.roundToInt

data class UserTasteProfile(
    val topCategories: List<Pair<String, Float>>,
    val topTags: List<String>,
    val amoledPreferencePct: Int,
    val totalInteractions: Int,
    val summaryVibe: String,
    val detailedAiExplanation: String
)

data class RecommendedWallpaper(
    val wallpaper: Wallpaper,
    val matchPercentage: Int,
    val matchReason: String
)

object AiRecommendationEngine {

    fun generateRecommendations(
        allWallpapers: List<Wallpaper>,
        interactions: List<UserInteractionEntity>,
        favorites: List<Wallpaper>,
        downloads: List<DownloadedEntity>
    ): Pair<UserTasteProfile, List<RecommendedWallpaper>> {
        val now = System.currentTimeMillis()

        // Combine explicit favorites and downloads if interaction log is sparse
        val categoryWeights = mutableMapOf<String, Float>()
        val tagWeights = mutableMapOf<String, Float>()
        var amoledPositivePoints = 0f
        var totalPoints = 0f

        // Process interactions
        interactions.forEach { interaction ->
            val baseWeight = when (interaction.interactionType) {
                "APPLIED" -> 6.0f
                "DOWNLOAD" -> 5.0f
                "FAVORITE" -> 4.0f
                "VIEW" -> 2.0f
                else -> 1.5f
            }

            val ageHours = (now - interaction.timestamp).coerceAtLeast(0) / (1000f * 60f * 60f)
            val recencyMultiplier = 0.4f + 0.6f * exp(-ageHours / 72f) // Half-life decay ~3 days
            val weight = baseWeight * recencyMultiplier

            categoryWeights[interaction.category] = (categoryWeights[interaction.category] ?: 0f) + weight

            val tags = interaction.tagsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            tags.forEach { tag ->
                tagWeights[tag] = (tagWeights[tag] ?: 0f) + weight
            }

            if (interaction.isAmoled) {
                amoledPositivePoints += weight
            }
            totalPoints += weight
        }

        // Incorporate current favorites with strong weight
        favorites.forEach { fav ->
            val weight = 4.0f
            categoryWeights[fav.category] = (categoryWeights[fav.category] ?: 0f) + weight
            fav.tags.forEach { tag ->
                tagWeights[tag] = (tagWeights[tag] ?: 0f) + weight
            }
            if (fav.isAmoled) amoledPositivePoints += weight
            totalPoints += weight
        }

        // Incorporate current downloads with high weight
        downloads.forEach { dl ->
            val weight = 5.0f
            categoryWeights[dl.category] = (categoryWeights[dl.category] ?: 0f) + weight
            totalPoints += weight
        }

        val hasEnoughData = totalPoints > 2.0f

        val sortedCategories = categoryWeights.entries
            .sortedByDescending { it.value }
            .map { it.key to (if (totalPoints > 0) it.value / totalPoints else 0f) }

        val sortedTags = tagWeights.entries
            .sortedByDescending { it.value }
            .take(6)
            .map { it.key }

        val amoledPct = if (totalPoints > 0) {
            ((amoledPositivePoints / totalPoints) * 100f).roundToInt().coerceIn(0, 100)
        } else {
            50
        }

        // Dynamic Vibe Generation
        val topCategory = sortedCategories.firstOrNull()?.first?.replaceFirstChar { it.uppercase() } ?: "Vibrant 4K"
        val secondCategory = sortedCategories.getOrNull(1)?.first?.replaceFirstChar { it.uppercase() }

        val summaryVibe = if (!hasEnoughData) {
            "Personalized AI Setup • Exploring Your Tastes"
        } else if (amoledPct >= 65) {
            if (secondCategory != null) "AMOLED & $topCategory ($secondCategory Focus)" else "True AMOLED Dark Master"
        } else if (secondCategory != null) {
            "$topCategory & $secondCategory Aesthetic"
        } else {
            "$topCategory Enthusiast"
        }

        val detailedAiExplanation = if (!hasEnoughData) {
            "AI Recommendation Engine is observing your views, downloads, and favorites. Explore wallpapers or tap ❤️ to sharpen your personalized recommendations."
        } else {
            val tagSummary = if (sortedTags.isNotEmpty()) " with preference for ${sortedTags.take(3).joinToString(", ")}" else ""
            "AI analyzed your activity across $topCategory${if (secondCategory != null) " and $secondCategory" else ""}$tagSummary. AMOLED affinity is rated at $amoledPct%."
        }

        val tasteProfile = UserTasteProfile(
            topCategories = sortedCategories,
            topTags = sortedTags,
            amoledPreferencePct = amoledPct,
            totalInteractions = interactions.size + favorites.size + downloads.size,
            summaryVibe = summaryVibe,
            detailedAiExplanation = detailedAiExplanation
        )

        // Score all wallpapers
        val scoredList = allWallpapers.map { wp ->
            var score = 0.0f
            val matchReasons = mutableListOf<String>()

            if (!hasEnoughData) {
                // Starter baseline recommendations
                if (wp.isFeatured) score += 40f
                if (wp.isTrending) score += 30f
                if (wp.isPopular) score += 25f
                if (wp.isAmoled) score += 20f
                score += (wp.downloadsCount / 1000f) * 1.5f
                matchReasons.add("Trending Spotlight")
            } else {
                // Category match
                val catAffinity = categoryWeights[wp.category] ?: 0f
                if (catAffinity > 0) {
                    score += catAffinity * 25f
                    matchReasons.add("Matches your ${wp.category.replaceFirstChar { it.uppercase() }} taste")
                }

                // Tag matches
                var matchingTagCount = 0
                wp.tags.forEach { tag ->
                    val tw = tagWeights[tag] ?: 0f
                    if (tw > 0) {
                        score += tw * 12f
                        matchingTagCount++
                    }
                }
                if (matchingTagCount > 0) {
                    matchReasons.add("Matches ${matchingTagCount} of your favorite tags")
                }

                // AMOLED Affinity
                if (wp.isAmoled && amoledPct >= 50) {
                    score += (amoledPct / 100f) * 35f
                    matchReasons.add("High AMOLED contrast match")
                }

                // Popularity/quality priors
                if (wp.isFeatured) score += 15f
                if (wp.isTrending) score += 12f
            }

            // Normalize match percentage between 75% and 99%
            val matchPct = (75 + (score % 24).toInt()).coerceIn(75, 99)
            val primaryReason = matchReasons.firstOrNull() ?: "Matches your aesthetic profile"

            RecommendedWallpaper(
                wallpaper = wp,
                matchPercentage = matchPct,
                matchReason = primaryReason
            )
        }.sortedByDescending { it.matchPercentage }

        return Pair(tasteProfile, scoredList)
    }
}
