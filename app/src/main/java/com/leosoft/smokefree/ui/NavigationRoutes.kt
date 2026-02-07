package com.leosoft.smokefree.ui

sealed class NavigationRoutes(val route: String, val label: String, val iconName: String) {
    data object Trophies : NavigationRoutes("trophies", "Kupalar", "badge_smoke_20")
    data object Rewards : NavigationRoutes("rewards", "Ödüller", "reward_gift")
    data object Progress : NavigationRoutes("progress", "İlerleme", "health_progress")
    data object Health : NavigationRoutes("health", "Sağlık", "health_heart")
    data object Motivation : NavigationRoutes("motivation", "Motivasyon", "mascot_brain")

    companion object {
        val items = listOf(Trophies, Rewards, Progress, Health, Motivation)
    }
}
