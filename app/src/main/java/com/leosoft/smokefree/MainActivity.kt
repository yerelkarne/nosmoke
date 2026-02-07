package com.leosoft.smokefree

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.leosoft.smokefree.notifications.NotificationHelper
import com.leosoft.smokefree.ui.BottomNavBar
import com.leosoft.smokefree.ui.HealthScreen
import com.leosoft.smokefree.ui.MessageDetailScreen
import com.leosoft.smokefree.ui.MessagesScreen
import com.leosoft.smokefree.ui.OnboardingScreen
import com.leosoft.smokefree.ui.ProgressScreen
import com.leosoft.smokefree.ui.RewardsScreen
import com.leosoft.smokefree.ui.SmokeFreeTheme
import com.leosoft.smokefree.ui.TrophiesScreen
import com.leosoft.smokefree.ui.NavigationRoutes
import com.leosoft.smokefree.ui.MotivationScreen

class MainActivity : ComponentActivity() {
    private val pendingMessageIdState = mutableStateOf(-1)
    private val pendingRouteState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingMessageIdState.value = intent?.getIntExtra(NotificationHelper.EXTRA_MESSAGE_ID, -1) ?: -1
        pendingRouteState.value = intent?.getStringExtra(NotificationHelper.EXTRA_ROUTE)
        setContent {
            SmokeFreeTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val statsRepository = remember { AppContainer.statsRepository(context) }
                    val stats by statsRepository.statsFlow.collectAsState(initial = null)
                    val onboardingCompleted = stats?.stats?.onboardingCompleted == true
                    val coroutineScope = rememberCoroutineScope()

                    if (!onboardingCompleted) {
                        OnboardingScreen(onComplete = {
                            coroutineScope.launch {
                                statsRepository.updateOnboardingCompleted(true)
                            }
                        })
                    } else {
                        LaunchedEffect(pendingMessageIdState.value) {
                            if (pendingMessageIdState.value != -1) {
                                navController.navigate("detail/${pendingMessageIdState.value}")
                                pendingMessageIdState.value = -1
                            }
                        }

                        LaunchedEffect(pendingRouteState.value) {
                            pendingRouteState.value?.let { route ->
                                navController.navigate(route)
                                pendingRouteState.value = null
                            }
                        }

                        Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
                            NavHost(
                                navController = navController,
                                startDestination = NavigationRoutes.Progress.route,
                                modifier = Modifier.padding(padding)
                            ) {
                                composable(NavigationRoutes.Trophies.route) { TrophiesScreen() }
                                composable(NavigationRoutes.Rewards.route) { RewardsScreen() }
                                composable(NavigationRoutes.Progress.route) { ProgressScreen() }
                                composable(NavigationRoutes.Health.route) { HealthScreen() }
                                composable(NavigationRoutes.Motivation.route) {
                                    MotivationScreen(
                                        onMessagesClick = { navController.navigate("list") }
                                    )
                                }
                                composable("list") {
                                    MessagesScreen(
                                        onBack = { navController.popBackStack() },
                                        onMessageClick = { id -> navController.navigate("detail/$id") }
                                    )
                                }
                                composable("detail/{id}") { backStackEntry ->
                                    val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                                    MessageDetailScreen(
                                        messageId = id,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingMessageIdState.value = intent.getIntExtra(NotificationHelper.EXTRA_MESSAGE_ID, -1)
        pendingRouteState.value = intent.getStringExtra(NotificationHelper.EXTRA_ROUTE)
    }
}
