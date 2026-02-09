package com.leosoft.smokefree.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
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
                        imageVector = getNavIcon(item.iconName),
                        contentDescription = stringResource(item.labelRes)
                    )
                },
                label = {
                    val labelText = stringResource(item.labelRes)
                    Text(
                        text = truncateNavLabel(labelText),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

private const val navLabelMaxLength = 8

private fun truncateNavLabel(label: String): String {
    return if (label.length <= navLabelMaxLength) {
        label
    } else {
        "${label.take(navLabelMaxLength)}…"
    }
}

private fun getNavIcon(name: String): ImageVector {
    return when (name) {
        "badge_smoke_20" -> Icons.Filled.EmojiEvents
        "reward_gift" -> Icons.Filled.CardGiftcard
        "health_progress" -> Icons.Filled.TrendingUp
        "health_heart" -> Icons.Filled.Favorite
        "mascot_brain" -> Icons.Filled.Psychology
        else -> Icons.Filled.EmojiEvents
    }
}
