package com.leosoft.smokefree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.leosoft.smokefree.AppContainer
import com.leosoft.smokefree.data.db.entities.RewardItem
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen() {
    val context = LocalContext.current
    val rewardRepository = remember { AppContainer.rewardRepository(context) }
    val statsRepository = remember { AppContainer.statsRepository(context) }
    val rewards by rewardRepository.observeRewards().collectAsState(initial = emptyList())
    val stats by statsRepository.statsFlow.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ödüller") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(painter = painterResource(android.R.drawable.ic_input_add), contentDescription = "Yeni ödül")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(rewards) { item ->
                val progress = stats?.let { s ->
                    (s.savedMoney / item.price).toFloat().coerceIn(0f, 1f)
                } ?: 0f
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Hedef: ${item.price}₺")
                        LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Yeni Hedef") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(value = title, onValueChange = { title = it }, label = { Text("Başlık") })
                    TextField(value = priceText, onValueChange = { priceText = it }, label = { Text("Fiyat (₺)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && price > 0) {
                        coroutineScope.launch {
                            rewardRepository.addReward(
                                RewardItem(
                                    id = "reward_${System.currentTimeMillis()}",
                                    title = title,
                                    price = price,
                                    iconName = "reward_gift",
                                    createdAt = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                    title = ""
                    priceText = ""
                    showDialog = false
                }) { Text("Kaydet") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("İptal") }
            }
        )
    }
}
