package com.leosoft.smokefree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.unit.dp
import com.leosoft.smokefree.AppContainer
import com.leosoft.smokefree.data.repository.DashboardStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen() {
    val context = LocalContext.current
    val statsRepository = remember { AppContainer.statsRepository(context) }
    val stats by statsRepository.statsFlow.collectAsState(initial = null)

    Scaffold(
        topBar = { TopAppBar(title = { Text("İlerleme") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            stats?.let {
                DashboardCard(title = "İçilmeyen Sigara", value = "${it.notSmokedCount}", progress = progressFor(it.notSmokedCount, 1000))
                DashboardCard(title = "Tasarruf Edilen Para", value = "${it.savedMoney.toInt()}₺", progress = progressFor(it.savedMoney.toLong(), 10000))
                DashboardCard(title = "Kazanılan Ömür", value = "${it.lifeDays} gün", progress = progressFor(it.lifeDays.toLong(), 30))
                DashboardCard(title = "Sigara İçilmeyen Süre", value = "${it.smokeFreeDays} gün", progress = progressFor(it.smokeFreeDays.toLong(), 30))
            } ?: Text("İlerleme verileri hazırlanıyor…")
        }
    }
}

@Composable
private fun DashboardCard(title: String, value: String, progress: Float) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(text = value, style = MaterialTheme.typography.headlineSmall)
            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
        }
    }
}

private fun progressFor(value: Long, target: Long): Float {
    if (target <= 0) return 0f
    return (value.toFloat() / target.toFloat()).coerceIn(0f, 1f)
}
