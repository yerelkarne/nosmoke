package com.leosoft.smokefree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.ConfirmationNumber
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leosoft.smokefree.data.db.entities.RewardItem
import com.leosoft.smokefree.ui.viewmodel.RewardsViewModel
import com.leosoft.smokefree.ui.viewmodel.ProgressViewModel
import kotlinx.coroutines.launch
import com.leosoft.smokefree.R
import com.leosoft.smokefree.ui.viewmodel.RewardItemState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen() {
    val viewModel: RewardsViewModel = viewModel()
    val progressViewModel: ProgressViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val progressState by progressViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<String?>(null) }
    var editingCreatedAt by remember { mutableStateOf<Long?>(null) }
    var editingIconName by remember { mutableStateOf("reward_gift") }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.title_rewards),
                onSettingsClick = { showSettingsDialog = true }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingId = null
                    editingCreatedAt = null
                    editingIconName = "reward_gift"
                    title = ""
                    priceText = ""
                    showDialog = true
                },
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
        ) { item ->
            editingId = item.id
            editingCreatedAt = item.createdAt
            editingIconName = item.iconName
            title = item.title
            priceText = item.price.toString()
            showDialog = true
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editingId == null) "Yeni Hedef" else "Hedefi Düzenle") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(value = title, onValueChange = { title = it }, label = { Text("Başlık") })
                    TextField(value = priceText, onValueChange = { priceText = it }, label = { Text("Fiyat (₺)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val trimmedTitle = title.trim()
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    if (trimmedTitle.isNotBlank() && price > 0) {
                        coroutineScope.launch {
                            viewModel.addReward(
                                RewardItem(
                                    id = editingId ?: "reward_${System.currentTimeMillis()}",
                                    title = trimmedTitle,
                                    price = price,
                                    iconName = editingIconName,
                                    createdAt = editingCreatedAt ?: System.currentTimeMillis()
                                )
                            )
                        }
                    }
                    title = ""
                    priceText = ""
                    editingId = null
                    editingCreatedAt = null
                    editingIconName = "reward_gift"
                    showDialog = false
                }) { Text("Kaydet") }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.s)) {
                    if (editingId != null) {
                        TextButton(onClick = { showDeleteConfirm = true }) { Text("Sil") }
                    }
                    TextButton(onClick = {
                        showDialog = false
                        editingId = null
                        editingCreatedAt = null
                        editingIconName = "reward_gift"
                    }) { Text("İptal") }
                }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Emin misiniz?") },
            text = { Text("Bu hedef silinecek.") },
            confirmButton = {
                TextButton(onClick = {
                    val id = editingId
                    if (id != null) {
                        coroutineScope.launch {
                            viewModel.deleteReward(id)
                        }
                    }
                    showDeleteConfirm = false
                    showDialog = false
                    editingId = null
                    editingCreatedAt = null
                    editingIconName = "reward_gift"
                    title = ""
                    priceText = ""
                }) { Text("Sil") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Vazgeç") }
            }
        )
    }

    SettingsDialog(
        show = showSettingsDialog,
        cigarettesPerDay = progressState.cigarettesPerDay,
        packPrice = progressState.packPrice,
        packSize = progressState.packSize,
        smokeFreeDays = progressState.smokeFreeDays,
        onDismiss = { showSettingsDialog = false },
        onSave = { startTimestamp, cigarettes, price, size ->
            progressViewModel.updateUserStats(startTimestamp, cigarettes, price, size)
            showSettingsDialog = false
        }
    )
}

@Composable
private fun RewardsContent(
    savedMoney: Double,
    items: List<RewardItemState>,
    modifier: Modifier = Modifier,
    onItemClick: (RewardItemState) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.m)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.s)) {
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.s), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CardGiftcard,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(text = "Mevcut birikim", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            Text(text = "${formatCurrency(savedMoney)}₺", style = MaterialTheme.typography.headlineLarge)
            }
        }
        items(items) { item ->
            RewardCard(
                title = item.title,
                priceText = "Hedef: ${item.price}₺",
                progress = item.progress,
                progressText = "Hedefe %${(item.progress * 100).toInt()} yaklaştın",
                icon = iconByRewardName(item.iconName),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item) }
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
                RewardItemState("1", "Kulaklık", 1500.0, "reward_phone", 0.28f, System.currentTimeMillis()),
                RewardItemState("2", "Bilet", 400.0, "reward_ticket", 0.9f, System.currentTimeMillis())
            ),
            modifier = Modifier.padding(AppSpacing.m),
            onItemClick = {}
        )
    }
}

private fun iconByRewardName(name: String): ImageVector {
    return when (name) {
        "reward_ticket" -> Icons.Filled.ConfirmationNumber
        "reward_shoes" -> Icons.Filled.Headphones
        "reward_phone" -> Icons.Filled.PhoneIphone
        else -> Icons.Filled.CardGiftcard
    }
}

private fun formatCurrency(value: Double): String {
    return "%.2f".format(value)
}
