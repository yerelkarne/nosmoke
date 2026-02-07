package com.leosoft.smokefree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.leosoft.smokefree.AppContainer
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen() {
    val context = LocalContext.current
    val healthRepository = remember { AppContainer.healthRepository(context) }
    val statsRepository = remember { AppContainer.statsRepository(context) }
    val milestones by healthRepository.observeMilestones().collectAsState(initial = emptyList())
    val stats by statsRepository.statsFlow.collectAsState(initial = null)

    val elapsedHours = stats?.elapsedMillis?.let { TimeUnit.MILLISECONDS.toHours(it).toInt() } ?: 0

    Scaffold(
        topBar = { TopAppBar(title = { Text("Sağlık") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(milestones) { milestone ->
                val isReached = elapsedHours >= milestone.triggerDurationHours
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            painter = painterResource(id = drawableByHealthIcon(milestone.iconName)),
                            contentDescription = milestone.title
                        )
                        Text(text = milestone.title, style = MaterialTheme.typography.titleMedium)
                        Text(text = milestone.description)
                        Text(text = if (isReached) "Tamamlandı" else "Yaklaşıyor")
                    }
                }
            }
        }
    }
}

private fun drawableByHealthIcon(name: String): Int {
    return when (name) {
        "health_heart" -> com.leosoft.smokefree.R.drawable.health_heart
        "health_lungs" -> com.leosoft.smokefree.R.drawable.health_lungs
        "health_timer" -> com.leosoft.smokefree.R.drawable.health_timer
        "health_progress" -> com.leosoft.smokefree.R.drawable.health_progress
        else -> com.leosoft.smokefree.R.drawable.health_heart
    }
}
