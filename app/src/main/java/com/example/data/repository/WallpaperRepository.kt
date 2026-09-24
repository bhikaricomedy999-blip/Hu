package com.example.data.repository

import android.content.Context
import com.example.data.local.CustomAiEntity
import com.example.data.local.DownloadedEntity
import com.example.data.local.FavoriteEntity
import com.example.data.local.WallpaperDatabase
import com.example.data.model.Wallpaper
import com.example.data.model.WallpaperCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WallpaperRepository(context: Context) {
    private val db = WallpaperDatabase.getDatabase(context)
    private val dao = db.wallpaperDao()

    val categories: List<WallpaperCategory> = listOf(
        WallpaperCategory(
            id = "amoled",
            name = "AMOLED & Dark",
            odiaName = "କଳା / ଆମୋଲେଡ୍",
            iconName = "Dark",
            coverImageUrl = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?auto=format&fit=crop&w=1080&q=85",
            wallpaperCount = 48,
            description = "True pitch black backgrounds optimized for OLED battery saving"
        ),
        WallpaperCategory(
            id = "nature",
            name = "Nature & Travel",
            odiaName = "ପ୍ରକୃତି ଓ ପର୍ଯ୍ୟଟନ",
            iconName = "Nature",
            coverImageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1080&q=85",
            wallpaperCount = 65,
            description = "Breathtaking landscapes, misty mountains, lush pine forests and waterfalls"
        ),
        WallpaperCategory(
            id = "cars",
            name = "Cars & Superbikes",
            odiaName = "କାର୍ ଓ ବାଇକ୍",
            iconName = "Speed",
            coverImageUrl = "https://images.unsplash.com/photo-1617814076367-b759c7d7e738?auto=format&fit=crop&w=1080&q=85",
            wallpaperCount = 52,
            description = "Exotic supercars, roaring sports bikes, retro muscle and sleek hypercars"
        ),
        WallpaperCategory(
            id = "gaming",
            name = "Gaming & Cyberpunk",
            odiaName = "ଗେମିଂ ଓ ସାଇବର",
            iconName = "Games",
            coverImageUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?auto=format&fit=crop&w=1080&q=85",
            wallpaperCount = 44,
            description = "Neon sci-fi aesthetics, futuristic warriors, controllers and fantasy concept art"
        ),
        WallpaperCategory(
            id = "animals",
            name = "Wildlife & Animals",
            odiaName = "ଜୀବଜନ୍ତୁ",
            iconName = "Pets",
            coverImageUrl = "https://images.unsplash.com/photo-1564349683136-77e08dba1ef6?auto=format&fit=crop&w=1080&q=85",
            wallpaperCount = 38,
            description = "Majestic big cats, wolves, aquatic life, exotic birds and adorable pets"
        ),
        WallpaperCategory(
            id = "sports",
            name = "Sports & Athletes",
            odiaName = "ଖେଳକୁଦ",
            iconName = "Sports",
            coverImageUrl = "https://images.unsplash.com/photo-1517649763962-0c623266ddc0?auto=format&fit=crop&w=1080&q=85",
            wallpaperCount = 35,
            description = "Cricket, football, basketball stadiums, adrenaline sports and fitness icons"
        ),
        WallpaperCategory(
            id = "luxury",
            name = "Luxury & Gold",
            odiaName = "ରାଜକୀୟ ଲକ୍ସୁରୀ",
            iconName = "Crown",
            coverImageUrl = "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=1080&q=85",
            wallpaperCount = 30,
            description = "Golden accents, royal minimalism, premium watches, silk and diamonds"
        ),
        WallpaperCategory(
            id = "technology",
            name = "Tech & Matrix",
            odiaName = "ଟେକ୍ନୋଲୋଜି",
            iconName = "Code",
            coverImageUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=1080&q=85",
            wallpaperCount = 42,
            description = "Motherboard circuits, glowing fiber optics, code matrix and AI neural nets"
        ),
        WallpaperCategory(
            id = "abstract",
            name = "Abstract & 3D Art",
            odiaName = "ଆବଷ୍ଟ୍ରାକ୍ଟ କଳା",
            iconName = "Palette",
            coverImageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=1080&q=85",
            wallpaperCount = 58,
            description = "Fluid gradients, 3D iridescent renders, surreal geometry and smoke spirals"
        ),
        WallpaperCategory(
            id = "devotional",
            name = "Devotional & Sacred",
            odiaName = "ଭକ୍ତି ଓ ଆଧ୍ୟାତ୍ମିକ",
            iconName = "Temple",
            coverImageUrl = "https://images.unsplash.com/photo-1609342122563-a43ac8917a3a?auto=format&fit=crop&w=1080&q=85",
            wallpaperCount = 40,
            description = "Sacred deities, divine light, diya lamps, incense clouds and holy shrines"
        ),
        WallpaperCategory(
            id = "indian_odia",
            name = "Indian & Odia Heritage",
            odiaName = "ଓଡ଼ିଆ ଓ ଭାରତୀୟ ଐତିହ୍ୟ",
            iconName = "India",
            coverImageUrl = "https://images.unsplash.com/photo-1627993077755-aa5ce89f3655?auto=format&fit=crop&w=1080&q=85",
            wallpaperCount = 36,
            description = "Konark Sun Temple wheel, Puri Jagannath heritage, Chilika Lake, Odissi culture"
        ),
        WallpaperCategory(
            id = "love",
            name = "Love & Romantic",
            odiaName = "ପ୍ରେମ ଓ ଯୋଡି",
            iconName = "Heart",
            coverImageUrl = "https://images.unsplash.com/photo-1518199266791-5375a83190b7?auto=format&fit=crop&w=1080&q=85",
            wallpaperCount = 32,
            description = "Sunset silhouettes, warm embraces, glowing hearts and romantic vistas"
        ),
        WallpaperCategory(
            id = "space",
            name = "Space & Galaxy",
            odiaName = "ମହାକାଶ ଓ ଗାଲାକ୍ସି",
            iconName = "Planet",
            coverImageUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1080&q=85",
            wallpaperCount = 49,
            description = "Deep cosmos, James Webb nebulae, spiral galaxies, lunar glow and astronauts"
        )
    )

    private val allWallpapersList: List<Wallpaper> = listOf(
        // AMOLED & Dark
        Wallpaper(
            id = "amoled_01",
            title = "Midnight Neon Wave",
            category = "amoled",
            imageUrl = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 14820,
            viewsCount = 39400,
            tags = listOf("AMOLED", "Neon", "Minimal", "Dark", "Wave"),
            colorHex = "#00F0FF",
            authorName = "Aesthetic Visions",
            description = "Deep OLED pitch black background with glowing electric cyan neon curves.",
            isTrending = true,
            isFeatured = true
        ),
        Wallpaper(
            id = "amoled_02",
            title = "Black Hole Eclipse",
            category = "amoled",
            imageUrl = "https://images.unsplash.com/photo-1507499739999-097706ad8914?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 11200,
            viewsCount = 28900,
            tags = listOf("AMOLED", "Eclipse", "Space", "Dark"),
            colorHex = "#FFB703",
            authorName = "Cosmic Studio",
            description = "Golden photon ring encircling a total gravitational singularity on pure black.",
            isPopular = true
        ),
        Wallpaper(
            id = "amoled_03",
            title = "Dark Carbon Fiber Prism",
            category = "amoled",
            imageUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?auto=format&fit=crop&w=1080&q=85",
            resolution = "QHD+",
            isAmoled = true,
            downloadsCount = 8900,
            viewsCount = 22100,
            tags = listOf("AMOLED", "Geometry", "Minimal", "Abstract"),
            colorHex = "#9D4EDD",
            authorName = "Minimal Lab",
            description = "Monochrome geometric facets reflecting violet rim light.",
            isNew = true
        ),

        // Nature & Landscape
        Wallpaper(
            id = "nature_01",
            title = "Misty Alpine Forest",
            category = "nature",
            imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = false,
            downloadsCount = 21400,
            viewsCount = 58200,
            tags = listOf("Nature", "Mountains", "Lake", "Forest", "Landscape"),
            colorHex = "#2EC4B6",
            authorName = "Wild Vista",
            description = "Crystal clear emerald mountain lake reflecting pine peaks draped in morning fog.",
            isTrending = true,
            isFeatured = true,
            isPopular = true
        ),
        Wallpaper(
            id = "nature_02",
            title = "Aurora Borealis Dream",
            category = "nature",
            imageUrl = "https://images.unsplash.com/photo-1531366936337-7c912a4589a7?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 18900,
            viewsCount = 49100,
            tags = listOf("Nature", "Aurora", "Norway", "Night", "Green"),
            colorHex = "#00FF87",
            authorName = "Nordic Lights",
            description = "Mesmerizing green dancing ribbons of Northern Lights across a star-studded sky.",
            isPopular = true
        ),
        Wallpaper(
            id = "nature_03",
            title = "Golden Desert Dunes",
            category = "nature",
            imageUrl = "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = false,
            downloadsCount = 7600,
            viewsCount = 19400,
            tags = listOf("Nature", "Desert", "Sahara", "Sunset", "Sand"),
            colorHex = "#E76F51",
            authorName = "Dune Explorer",
            description = "Wind-swept razor sharp sand ridges illuminated by dramatic sunset shadows.",
            isNew = true
        ),

        // Cars & Bikes
        Wallpaper(
            id = "cars_01",
            title = "Tokyo Cyber Superbike",
            category = "cars",
            imageUrl = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 26500,
            viewsCount = 67800,
            tags = listOf("Bikes", "Superbike", "Tokyo", "Night", "Motorcycle"),
            colorHex = "#FF0055",
            authorName = "Speed Demon",
            description = "Aggressive matte black supersport motorcycle parked on rain-slicked Tokyo asphalt.",
            isTrending = true,
            isFeatured = true
        ),
        Wallpaper(
            id = "cars_02",
            title = "Aventador Neon Beast",
            category = "cars",
            imageUrl = "https://images.unsplash.com/photo-1617814076367-b759c7d7e738?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = false,
            downloadsCount = 23100,
            viewsCount = 59000,
            tags = listOf("Cars", "Supercar", "Lamborghini", "Hypercar"),
            colorHex = "#FFB703",
            authorName = "Exotic Renders",
            description = "Sculpted Italian hypercar under moody studio spotlight with carbon aero accents.",
            isPopular = true
        ),
        Wallpaper(
            id = "cars_03",
            title = "Vintage Porsche 911 Turbo",
            category = "cars",
            imageUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=1080&q=85",
            resolution = "FHD+",
            isAmoled = false,
            downloadsCount = 9800,
            viewsCount = 24500,
            tags = listOf("Cars", "Classic", "Porsche", "Retro"),
            colorHex = "#3A86FF",
            authorName = "Heritage Motors",
            description = "Timeless silhouette of an iconic air-cooled sports car cruising at dusk.",
            isNew = true
        ),

        // Gaming
        Wallpaper(
            id = "gaming_01",
            title = "Cyber Samurai 2099",
            category = "gaming",
            imageUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 19800,
            viewsCount = 51200,
            tags = listOf("Gaming", "Cyberpunk", "Neon", "Samurai"),
            colorHex = "#FF007F",
            authorName = "Pixel Blade",
            description = "Futuristic cyber warrior with holographic katana glowing in rain-soaked alley.",
            isTrending = true
        ),
        Wallpaper(
            id = "gaming_02",
            title = "Neon Gamepad Horizon",
            category = "gaming",
            imageUrl = "https://images.unsplash.com/photo-1600080972464-8e5f35f63d08?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 14300,
            viewsCount = 37600,
            tags = listOf("Gaming", "Controller", "PlayStation", "RGB"),
            colorHex = "#8338EC",
            authorName = "Respawn Lab",
            description = "Sleek pro controller hovering amid ultraviolet particles and laser lines.",
            isPopular = true
        ),

        // Animals & Wildlife
        Wallpaper(
            id = "animals_01",
            title = "Royal Bengal Tiger Eyes",
            category = "animals",
            imageUrl = "https://images.unsplash.com/photo-1564349683136-77e08dba1ef6?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 24100,
            viewsCount = 61000,
            tags = listOf("Animals", "Tiger", "Wildlife", "Predator", "India"),
            colorHex = "#FB8500",
            authorName = "Sundarbans Safari",
            description = "Intense piercing amber gaze of a Bengal tiger emerging from dark jungle shadows.",
            isFeatured = true,
            isPopular = true
        ),
        Wallpaper(
            id = "animals_02",
            title = "Black Panther Stealth",
            category = "animals",
            imageUrl = "https://images.unsplash.com/photo-1557008075-7f2c5efa4cfd?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 16800,
            viewsCount = 42300,
            tags = listOf("Animals", "Panther", "AMOLED", "Cat"),
            colorHex = "#00B4D8",
            authorName = "Apex Hunter",
            description = "Glossy midnight coat of a black leopard illuminated by moonlight rim highlights.",
            isTrending = true
        ),

        // Sports
        Wallpaper(
            id = "sports_01",
            title = "Cricket Stadium Under Floodlights",
            category = "sports",
            imageUrl = "https://images.unsplash.com/photo-1531415074968-036ba1b575da?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = false,
            downloadsCount = 17500,
            viewsCount = 44000,
            tags = listOf("Sports", "Cricket", "Stadium", "Lights", "Match"),
            colorHex = "#48CAE4",
            authorName = "Pitch View",
            description = "Colossal stadium bathed in intense white floodlights before an electric match.",
            isPopular = true
        ),
        Wallpaper(
            id = "sports_02",
            title = "Champions Football Arena",
            category = "sports",
            imageUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = false,
            downloadsCount = 13200,
            viewsCount = 33800,
            tags = listOf("Sports", "Football", "Soccer", "Stadium"),
            colorHex = "#06D6A0",
            authorName = "Goal Post",
            description = "Pristine emerald grass field and dramatic night sky over the grandstands.",
            isNew = true
        ),

        // Luxury
        Wallpaper(
            id = "luxury_01",
            title = "Golden Silk Flow",
            category = "luxury",
            imageUrl = "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 15300,
            viewsCount = 38000,
            tags = listOf("Luxury", "Gold", "Silk", "Minimal", "AMOLED"),
            colorHex = "#FFD166",
            authorName = "Midas Atelier",
            description = "Liquid 24-karat gold waves draped smoothly across velvet blackness.",
            isFeatured = true
        ),
        Wallpaper(
            id = "luxury_02",
            title = "Midnight Chronograph",
            category = "luxury",
            imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 11900,
            viewsCount = 29400,
            tags = listOf("Luxury", "Watch", "Timepiece", "Craftsmanship"),
            colorHex = "#E0E1DD",
            authorName = "Haute Horlogerie",
            description = "Precision tourbillon movement with sapphire glass and brushed titanium bezel.",
            isTrending = true
        ),

        // Technology
        Wallpaper(
            id = "technology_01",
            title = "Neural Quantum Circuit",
            category = "technology",
            imageUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 20500,
            viewsCount = 52300,
            tags = listOf("Tech", "Circuit", "Quantum", "AI", "Cyber"),
            colorHex = "#00F0FF",
            authorName = "Cyber Matrix",
            description = "Microscopic silicon tracks pulsing with luminous cyan data streams.",
            isTrending = true,
            isPopular = true
        ),

        // Abstract & Artistic
        Wallpaper(
            id = "abstract_01",
            title = "Holographic Liquid Glass",
            category = "abstract",
            imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = false,
            downloadsCount = 18600,
            viewsCount = 47800,
            tags = listOf("Abstract", "3D", "Iridescent", "Glass", "Modern"),
            colorHex = "#C77DFF",
            authorName = "Prism Works",
            description = "Flowing ribbons of rainbow glass with refraction caustic reflections.",
            isFeatured = true,
            isPopular = true
        ),
        Wallpaper(
            id = "abstract_02",
            title = "Neon Smoke Vortex",
            category = "abstract",
            imageUrl = "https://images.unsplash.com/photo-1541701494587-cb58502866ab?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 16200,
            viewsCount = 41000,
            tags = listOf("Abstract", "Smoke", "Vibrant", "Color"),
            colorHex = "#F72585",
            authorName = "Flow Art",
            description = "Electric magenta and cyan pigment smoke colliding in high speed dark fluid.",
            isTrending = true
        ),

        // Devotional
        Wallpaper(
            id = "devotional_01",
            title = "Divine Diya of Light",
            category = "devotional",
            imageUrl = "https://images.unsplash.com/photo-1609342122563-a43ac8917a3a?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 27800,
            viewsCount = 72100,
            tags = listOf("Devotional", "Diya", "Spiritual", "Aura", "Prayer"),
            colorHex = "#FFB703",
            authorName = "Bhakti Studio",
            description = "Sacred brass deepam glowing brightly with holy aura in twilight sanctuary.",
            isFeatured = true,
            isPopular = true
        ),
        Wallpaper(
            id = "devotional_02",
            title = "Ancient Shivalinga in Mist",
            category = "devotional",
            imageUrl = "https://images.unsplash.com/photo-1545128485-c400e7702796?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = false,
            downloadsCount = 22400,
            viewsCount = 57300,
            tags = listOf("Devotional", "Shiva", "Temple", "Himalayas"),
            colorHex = "#4895EF",
            authorName = "Kailash Darshan",
            description = "Tranquil stone shrine carved in Himalayan heights surrounded by sacred bells.",
            isTrending = true
        ),

        // Indian & Odia Heritage
        Wallpaper(
            id = "indian_odia_01",
            title = "Konark Sun Temple Chariot Wheel",
            category = "indian_odia",
            imageUrl = "https://images.unsplash.com/photo-1627993077755-aa5ce89f3655?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = false,
            downloadsCount = 25900,
            viewsCount = 68400,
            tags = listOf("Odia", "Konark", "SunTemple", "Odisha", "Heritage", "Architecture"),
            colorHex = "#E09F3E",
            authorName = "Utkala Heritage",
            description = "Magnificent stone carved Konark wheel sundial capturing ancient Kalinga mastery.",
            isFeatured = true,
            isTrending = true,
            isPopular = true
        ),
        Wallpaper(
            id = "indian_odia_02",
            title = "Puri Golden Sea Beach at Dawn",
            category = "indian_odia",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = false,
            downloadsCount = 19100,
            viewsCount = 49200,
            tags = listOf("Odia", "Puri", "Beach", "BayOfBengal", "Sunrise"),
            colorHex = "#F4A261",
            authorName = "Jagannath Dham",
            description = "Sacred sunrise golden waves touching the shores of holy Puri Jagannath dham.",
            isPopular = true
        ),
        Wallpaper(
            id = "indian_odia_03",
            title = "Chilika Lake Migratory Haven",
            category = "indian_odia",
            imageUrl = "https://images.unsplash.com/photo-1518457607834-6e8d80c183c5?auto=format&fit=crop&w=1080&q=85",
            resolution = "FHD+",
            isAmoled = false,
            downloadsCount = 14200,
            viewsCount = 36700,
            tags = listOf("Odia", "Chilika", "Lagoon", "Birds", "Nature"),
            colorHex = "#2A9D8F",
            authorName = "Odisha Tourism",
            description = "Peaceful wooden boats floating on Asia's largest brackish lagoon under pastel skies.",
            isNew = true
        ),

        // Love & Couple
        Wallpaper(
            id = "love_01",
            title = "Starlit Silhouette Romance",
            category = "love",
            imageUrl = "https://images.unsplash.com/photo-1518199266791-5375a83190b7?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 18400,
            viewsCount = 47200,
            tags = listOf("Love", "Couple", "Sunset", "Romance", "Silhouette"),
            colorHex = "#FF4D6D",
            authorName = "Soulmates",
            description = "Romantic couple hand-in-hand beneath a canopy of glowing fairy lights and twilight sky.",
            isFeatured = true,
            isTrending = true
        ),

        // Space & Universe
        Wallpaper(
            id = "space_01",
            title = "Cosmic Carina Nebula",
            category = "space",
            imageUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 28900,
            viewsCount = 74500,
            tags = listOf("Space", "Galaxy", "Nebula", "Cosmos", "Stars"),
            colorHex = "#7209B7",
            authorName = "NASA Deep Field",
            description = "Stupendous stellar nursery with luminous interstellar dust clouds and glowing gas pillars.",
            isFeatured = true,
            isPopular = true,
            isTrending = true
        ),
        Wallpaper(
            id = "space_02",
            title = "Moonlit Earth from Orbit",
            category = "space",
            imageUrl = "https://images.unsplash.com/photo-1614728894747-a83421e2b9c9?auto=format&fit=crop&w=1080&q=85",
            resolution = "4K UHD",
            isAmoled = true,
            downloadsCount = 21200,
            viewsCount = 54100,
            tags = listOf("Space", "Earth", "Orbit", "Astronomy", "Moon"),
            colorHex = "#4CC9F0",
            authorName = "ISS Orbit",
            description = "The blue marble horizon glowing gently against infinite dark void of outer space.",
            isPopular = true
        )
    )

    fun getAllWallpapers(): List<Wallpaper> = allWallpapersList

    fun getFeaturedWallpapers(): List<Wallpaper> = allWallpapersList.filter { it.isFeatured }

    fun getTrendingWallpapers(): List<Wallpaper> = allWallpapersList.filter { it.isTrending }

    fun getPopularWallpapers(): List<Wallpaper> = allWallpapersList.filter { it.isPopular }

    fun getNewWallpapers(): List<Wallpaper> = allWallpapersList.filter { it.isNew }

    fun getAmoledWallpapers(): List<Wallpaper> = allWallpapersList.filter { it.isAmoled }

    fun getWallpapersByCategory(categoryId: String): List<Wallpaper> {
        return allWallpapersList.filter { it.category.equals(categoryId, ignoreCase = true) }
    }

    fun getWallpaperById(id: String): Wallpaper? {
        return allWallpapersList.find { it.id == id }
    }

    fun searchWallpapers(query: String, filterResolution: String? = null, isAmoledOnly: Boolean = false): List<Wallpaper> {
        val q = query.trim().lowercase()
        return allWallpapersList.filter { wp ->
            val matchesQuery = q.isEmpty() ||
                    wp.title.lowercase().contains(q) ||
                    wp.category.lowercase().contains(q) ||
                    wp.tags.any { it.lowercase().contains(q) } ||
                    wp.description.lowercase().contains(q)

            val matchesResolution = filterResolution == null || filterResolution == "All" || wp.resolution.contains(filterResolution, ignoreCase = true)
            val matchesAmoled = !isAmoledOnly || wp.isAmoled

            matchesQuery && matchesResolution && matchesAmoled
        }
    }

    // Room Favorites
    fun getFavorites(): Flow<List<Wallpaper>> {
        return dao.getAllFavorites().map { entities ->
            entities.map { entity ->
                getWallpaperById(entity.wallpaperId) ?: Wallpaper(
                    id = entity.wallpaperId,
                    title = entity.title,
                    category = entity.category,
                    imageUrl = entity.imageUrl,
                    thumbnailUrl = entity.thumbnailUrl,
                    resolution = entity.resolution,
                    isAmoled = entity.isAmoled
                )
            }
        }
    }

    fun isFavorite(id: String): Flow<Boolean> = dao.isFavorite(id)

    suspend fun toggleFavorite(wallpaper: Wallpaper, isFav: Boolean) {
        if (isFav) {
            dao.deleteFavorite(wallpaper.id)
        } else {
            dao.insertFavorite(
                FavoriteEntity(
                    wallpaperId = wallpaper.id,
                    title = wallpaper.title,
                    category = wallpaper.category,
                    imageUrl = wallpaper.imageUrl,
                    thumbnailUrl = wallpaper.thumbnailUrl,
                    resolution = wallpaper.resolution,
                    isAmoled = wallpaper.isAmoled
                )
            )
            recordInteraction(wallpaper, "FAVORITE")
        }
    }

    // Room Downloads
    fun getDownloads(): Flow<List<DownloadedEntity>> = dao.getAllDownloads()

    suspend fun saveDownload(wallpaper: Wallpaper, localPath: String) {
        dao.insertDownload(
            DownloadedEntity(
                wallpaperId = wallpaper.id,
                title = wallpaper.title,
                category = wallpaper.category,
                imageUrl = wallpaper.imageUrl,
                localUri = localPath,
                resolution = wallpaper.resolution
            )
        )
        recordInteraction(wallpaper, "DOWNLOAD")
    }

    suspend fun deleteDownload(id: String) {
        dao.deleteDownload(id)
    }

    // Custom AI Wallpapers
    fun getAiWallpapers(): Flow<List<CustomAiEntity>> = dao.getAllAiWallpapers()

    suspend fun saveAiWallpaper(aiEntity: CustomAiEntity) {
        dao.insertAiWallpaper(aiEntity)
    }

    suspend fun deleteAiWallpaper(id: String) {
        dao.deleteAiWallpaper(id)
    }

    // AI Recommendation Engine Interactions
    suspend fun recordInteraction(wallpaper: Wallpaper, interactionType: String) {
        dao.insertInteraction(
            com.example.data.local.UserInteractionEntity(
                wallpaperId = wallpaper.id,
                category = wallpaper.category,
                tagsString = wallpaper.tags.joinToString(","),
                isAmoled = wallpaper.isAmoled,
                interactionType = interactionType
            )
        )
    }

    fun getRecentInteractions(): Flow<List<com.example.data.local.UserInteractionEntity>> =
        dao.getRecentInteractions()

    suspend fun clearInteractions() {
        dao.clearAllInteractions()
    }
}
