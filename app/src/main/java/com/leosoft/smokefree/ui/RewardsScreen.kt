package com.leosoft.smokefree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leosoft.smokefree.data.db.entities.RewardItem
import com.leosoft.smokefree.ui.viewmodel.RewardsViewModel
import kotlinx.coroutines.launch
import com.leosoft.smokefree.R
import com.leosoft.smokefree.ui.viewmodel.RewardItemState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen() {
    val viewModel: RewardsViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.title_rewards)) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.tertiary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(painter = painterResource(android.R.drawable.ic_input_add), contentDescription = "Yeni ödül")
            }
        }
    ) { padding ->
        RewardsContent(
            savedMoney = state.savedMoney,
            items = state.items,
            modifier = Modifier
                .padding(padding)
                .padding(AppSpacing.m)
        )
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
                            viewModel.addReward(
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

@Composable
private fun RewardsContent(savedMoney: Double, items: List<RewardItemState>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.m)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.s)) {
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.s), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(com.leosoft.smokefree.R.drawable.reward_gift),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(text = "Mevcut birikim", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(text = "${savedMoney.toInt()}₺", style = MaterialTheme.typography.headlineLarge)
            }
        }
        items(items) { item ->
            RewardCard(
                title = item.title,
                priceText = "Hedef: ${item.price}₺",
                progress = item.progress,
                progressText = "Hedefe %${(item.progress * 100).toInt()} yaklaştın",
                iconRes = drawableByRewardIcon(item.iconName),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RewardsPreview() {
    SmokeFreeTheme {
        RewardsContent(
            savedMoney = 420.0,
            items = listOf(
                RewardItemState("1", "Kulaklık", 1500.0, "reward_phone", 0.28f),
                RewardItemState("2", "Bilet", 400.0, "reward_ticket", 0.9f)
            ),
            modifier = Modifier.padding(AppSpacing.m)
        )
    }
}

private fun drawableByRewardIcon(name: String): Int {
    return when (name) {
        "reward_ticket" -> com.leosoft.smokefree.R.drawable.reward_ticket
        "reward_shoes" -> com.leosoft.smokefree.R.drawable.reward_shoes
        "reward_phone" -> com.leosoft.smokefree.R.drawable.reward_phone
        else -> com.leosoft.smokefree.R.drawable.reward_gift
    }
}
