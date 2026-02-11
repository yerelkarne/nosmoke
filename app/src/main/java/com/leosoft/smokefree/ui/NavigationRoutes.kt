package com.leosoft.smokefree.ui

sealed class NavigationRoutes(val route: String, val labelRes: Int, val iconName: String) {
    data object Trophies : NavigationRoutes("trophies", com.leosoft.smokefree.R.string.tab_trophies, "badge_smoke_20")
    data object Rewards : NavigationRoutes("rewards", com.leosoft.smokefree.R.string.tab_rewards, "reward_gift")
    data object Progress : NavigationRoutes("progress", com.leosoft.smokefree.R.string.tab_progress, "health_progress")
    data object Health : NavigationRoutes("health", com.leosoft.smokefree.R.string.tab_health, "health_heart")
    data object Motivation : NavigationRoutes("motivation", com.leosoft.smokefree.R.string.tab_motivation, "mascot_brain")

    companion object {
        val items = listOf(Trophies, Rewards, Progress, Health, Motivation)
    }
}
