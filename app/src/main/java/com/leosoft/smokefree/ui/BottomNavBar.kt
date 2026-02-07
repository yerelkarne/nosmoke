package com.leosoft.smokefree.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationRoutes.items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(NavigationRoutes.Progress.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = getDrawableId(item.iconName)),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}

private fun getDrawableId(name: String): Int {
    return when (name) {
        "badge_smoke_20" -> com.leosoft.smokefree.R.drawable.badge_smoke_20
        "reward_gift" -> com.leosoft.smokefree.R.drawable.reward_gift
        "health_progress" -> com.leosoft.smokefree.R.drawable.health_progress
        "health_heart" -> com.leosoft.smokefree.R.drawable.health_heart
        "mascot_brain" -> com.leosoft.smokefree.R.drawable.mascot_brain
        else -> com.leosoft.smokefree.R.drawable.badge_smoke_20
    }
}
