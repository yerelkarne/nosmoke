package com.leosoft.smokefree

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.leosoft.smokefree.notifications.NotificationHelper
import com.leosoft.smokefree.ui.MessageDetailScreen
import com.leosoft.smokefree.ui.MessagesScreen
import com.leosoft.smokefree.ui.HomeScreen
import com.leosoft.smokefree.ui.SmokeFreeTheme

class MainActivity : ComponentActivity() {
    private val pendingMessageIdState = mutableStateOf(-1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingMessageIdState.value = intent?.getIntExtra(NotificationHelper.EXTRA_MESSAGE_ID, -1) ?: -1
        setContent {
            SmokeFreeTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    LaunchedEffect(pendingMessageIdState.value) {
                        if (pendingMessageIdState.value != -1) {
                            navController.navigate("detail/${pendingMessageIdState.value}")
                            pendingMessageIdState.value = -1
                        }
                    }

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingMessageIdState.value = intent.getIntExtra(NotificationHelper.EXTRA_MESSAGE_ID, -1)
    }
}
