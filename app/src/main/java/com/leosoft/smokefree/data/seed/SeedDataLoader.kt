package com.leosoft.smokefree.data.seed

import android.content.Context
import com.leosoft.smokefree.data.db.entities.AchievementDefinition
import com.leosoft.smokefree.data.db.entities.HealthMilestone
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class SeedDataLoader(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    fun loadAchievementDefinitions(): List<AchievementDefinition> {
        context.assets.open("achievement_definitions.json").use { inputStream ->
            val text = inputStream.bufferedReader().readText()
            val items = json.decodeFromString<List<SeedAchievementDefinition>>(text)
            return items.map { seed ->
                AchievementDefinition(
                    id = seed.id,
                    title = seed.title,
                    category = seed.category,
                    targetValue = seed.targetValue,
                    metricType = seed.metricType,
                    iconName = seed.iconName,
                    isPremium = seed.isPremium
                )
            }
        }
    }

    fun loadHealthMilestones(): List<HealthMilestone> {
        return listOf(
            HealthMilestone("m20", "20 dakika", "Kan basıncı düşer.", 0, "health_heart"),
            HealthMilestone("m8h", "8 saat", "CO seviyesi normale döner.", 8, "health_lungs"),
            HealthMilestone("m24h", "24 saat", "Kalp krizi riski azalır.", 24, "health_heart"),
            HealthMilestone("m48h", "48 saat", "Tat ve koku geri gelir.", 48, "health_progress"),
            HealthMilestone("m72h", "72 saat", "Solunum rahatlar.", 72, "health_lungs"),
            HealthMilestone("m5d", "5 gün", "Sigara isteği azalır.", 120, "health_timer"),
            HealthMilestone("m2w", "2 hafta", "Dolaşım artar.", 336, "health_heart"),
            HealthMilestone("m1m", "1 ay", "Stres azalır.", 720, "health_progress"),
            HealthMilestone("m9m", "9 ay", "Öksürük azalır.", 6480, "health_lungs"),
            HealthMilestone("m5y", "5 yıl", "Kalp riski yarıya iner.", 43800, "health_heart"),
            HealthMilestone("m10y", "10 yıl", "Akciğer kanseri riski düşer.", 87600, "health_lungs")
        )
    }
}

@Serializable
data class SeedAchievementDefinition(
    val id: String,
    val title: String,
    val category: String,
    val targetValue: Long,
    val metricType: String,
    val iconName: String,
    val isPremium: Boolean
)
