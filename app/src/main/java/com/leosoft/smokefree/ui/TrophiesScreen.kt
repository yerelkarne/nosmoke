package com.leosoft.smokefree.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.leosoft.smokefree.AppContainer
import com.leosoft.smokefree.data.db.entities.AchievementDefinition
import com.leosoft.smokefree.data.db.entities.AchievementProgress
import kotlinx.coroutines.flow.combine

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TrophiesScreen() {
    val context = LocalContext.current
    val repository = remember { AppContainer.achievementRepository(context) }
    val combinedFlow = remember {
        repository.observeDefinitions().combine(repository.observeProgress()) { definitions, progress ->
            val progressMap = progress.associateBy { it.achievementId }
            definitions.map { definition ->
                TrophiesUi(definition, progressMap[definition.id])
            }
        }
    }
    val items by combinedFlow.collectAsState(initial = emptyList())
    var selected by remember { mutableStateOf<TrophiesUi?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Kupalar") }) }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                Card(onClick = { selected = item }) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val alpha = if (item.progress?.isUnlocked == true) 1f else 0.4f
                        Icon(
                            painter = painterResource(id = drawableByName(item.definition.iconName)),
                            contentDescription = item.definition.title,
                            modifier = Modifier.alpha(alpha)
                        )
                        Text(
                            text = item.definition.title,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.alpha(alpha)
                        )
                    }
                }
            }
        }
    }

    selected?.let { item ->
        val progress = item.progress
        val progressValue = progress?.progressValue ?: 0L
        val percent = if (item.definition.targetValue == 0L) 0f else {
            (progressValue.toFloat() / item.definition.targetValue.toFloat()).coerceIn(0f, 1f)
        }
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(item.definition.title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "${item.definition.category} kategorisi")
                    Text(text = "İlerleme: ${(percent * 100).toInt()}%")
                }
            },
            confirmButton = {
                TextButton(onClick = { selected = null }) { Text("Kapat") }
            }
        )
    }
}

data class TrophiesUi(
    val definition: AchievementDefinition,
    val progress: AchievementProgress?
)

private fun drawableByName(name: String): Int {
    return when (name) {
        "badge_smoke_20" -> com.leosoft.smokefree.R.drawable.badge_smoke_20
        "badge_smoke_100" -> com.leosoft.smokefree.R.drawable.badge_smoke_100
        "badge_smoke_1000" -> com.leosoft.smokefree.R.drawable.badge_smoke_1000
        "badge_smoke_10000" -> com.leosoft.smokefree.R.drawable.badge_smoke_10000
        "badge_days_1" -> com.leosoft.smokefree.R.drawable.badge_days_1
        "badge_days_3" -> com.leosoft.smokefree.R.drawable.badge_days_3
        "badge_days_7" -> com.leosoft.smokefree.R.drawable.badge_days_7
        "badge_days_10" -> com.leosoft.smokefree.R.drawable.badge_days_10
        "badge_days_14" -> com.leosoft.smokefree.R.drawable.badge_days_14
        "badge_days_30" -> com.leosoft.smokefree.R.drawable.badge_days_30
        "badge_life_1" -> com.leosoft.smokefree.R.drawable.badge_life_1
        "badge_life_3" -> com.leosoft.smokefree.R.drawable.badge_life_3
        "badge_life_7" -> com.leosoft.smokefree.R.drawable.badge_life_7
        "badge_life_10" -> com.leosoft.smokefree.R.drawable.badge_life_10
        "badge_life_14" -> com.leosoft.smokefree.R.drawable.badge_life_14
        "badge_life_30" -> com.leosoft.smokefree.R.drawable.badge_life_30
        "achievement_decision" -> com.leosoft.smokefree.R.drawable.achievement_decision
        "achievement_premium" -> com.leosoft.smokefree.R.drawable.achievement_premium
        else -> com.leosoft.smokefree.R.drawable.badge_smoke_20
    }
}
