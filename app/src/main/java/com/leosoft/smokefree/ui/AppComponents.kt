package com.leosoft.smokefree.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onSettingsClick: () -> Unit = {},
    actions: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Ayarlar",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            actions()
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun SettingsDialog(
    show: Boolean,
    cigarettesPerDay: Int,
    packPrice: Int,
    packSize: Int,
    smokeFreeDays: Int,
    onDismiss: () -> Unit,
    onSave: (startTimestamp: Long, cigarettesPerDay: Int, packPrice: Int, packSize: Int) -> Unit
) {
    if (!show) return

    val cigarettesPerDayInput = remember(show) { mutableStateOf(cigarettesPerDay.toString()) }
    val packPriceInput = remember(show) { mutableStateOf(packPrice.toString()) }
    val packSizeInput = remember(show) { mutableStateOf(packSize.toString()) }
    val smokeFreeDaysInput = remember(show) { mutableStateOf(smokeFreeDays.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Ayarlar") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.s)) {
                TextField(
                    value = cigarettesPerDayInput.value,
                    onValueChange = { cigarettesPerDayInput.value = it },
                    label = { Text("Günde kaç sigara") }
                )
                TextField(
                    value = packPriceInput.value,
                    onValueChange = { packPriceInput.value = it },
                    label = { Text("Paket fiyatı (₺)") }
                )
                TextField(
                    value = packSizeInput.value,
                    onValueChange = { packSizeInput.value = it },
                    label = { Text("Paket adedi") }
                )
                TextField(
                    value = smokeFreeDaysInput.value,
                    onValueChange = { smokeFreeDaysInput.value = it },
                    label = { Text("Sigarasız geçen gün") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val cigarettes = cigarettesPerDayInput.value.toIntOrNull()
                        ?.coerceAtLeast(0) ?: cigarettesPerDay
                    val price = packPriceInput.value.toIntOrNull()
                        ?.coerceAtLeast(0) ?: packPrice
                    val size = packSizeInput.value.toIntOrNull()
                        ?.coerceAtLeast(1) ?: packSize
                    val smokeFreeDaysValue = smokeFreeDaysInput.value.toLongOrNull()
                        ?.coerceAtLeast(0L) ?: smokeFreeDays.toLong()
                    val startTimestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(smokeFreeDaysValue)
                    onSave(startTimestamp, cigarettes, price, size)
                }
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Vazgeç") }
        }
    )
}

@Composable
fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(AppSpacing.m),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.s)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(text = value, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgeCard(
    title: String,
    progress: Float,
    icon: ImageVector,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "badgeProgress")
    Card(
        modifier = modifier.height(160.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.m),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.s)
        ) {
            val badgeIconSize = 44.dp
            val alpha = if (isUnlocked) 1f else 0.55f
            Box {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(badgeIconSize).alpha(alpha),
                    tint = if (isUnlocked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isUnlocked) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Unlocked",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.align(Alignment.TopEnd).size(32.dp)
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.alpha(alpha)
            )
            LinearProgressIndicator(progress = animatedProgress, modifier = Modifier.fillMaxWidth())
            if (!isUnlocked) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Locked",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.requiredSize(badgeIconSize)
                )
            }
        }
    }
}

@Composable
fun RewardCard(
    title: String,
    priceText: String,
    progress: Float,
    progressText: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "rewardProgress")
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.m),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.s),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Text(text = priceText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LinearProgressIndicator(progress = animatedProgress, modifier = Modifier.fillMaxWidth())
                Text(text = progressText, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun MilestoneCard(
    title: String,
    description: String,
    progress: Float,
    statusText: String,
    icon: ImageVector,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "milestoneProgress")
    val progressColor = if (isCompleted) Color(0xFF2ECC71) else MaterialTheme.colorScheme.secondary
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.m),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.s)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.s)) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = progressColor,
                    modifier = Modifier.size(28.dp)
                )
                Text(text = title, style = MaterialTheme.typography.titleLarge)
            }
            Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            LinearProgressIndicator(progress = animatedProgress, color = progressColor, modifier = Modifier.fillMaxWidth())
            Text(text = statusText, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun StatCardPreview() {
    SmokeFreeTheme {
        StatCard(
            icon = androidx.compose.material.icons.Icons.Filled.Timer,
            value = "24 saat",
            label = "Sigara içmiyorum",
            modifier = Modifier.fillMaxWidth().padding(AppSpacing.m)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun BadgeCardPreview() {
    SmokeFreeTheme {
        BadgeCard(
            title = "7 gün sigarasız",
            progress = 0.6f,
            icon = androidx.compose.material.icons.Icons.Filled.EmojiEvents,
            isUnlocked = false,
            modifier = Modifier.padding(AppSpacing.m)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun RewardCardPreview() {
    SmokeFreeTheme {
        RewardCard(
            title = "Yeni Ayakkabı",
            priceText = "Hedef: 1500₺",
            progress = 0.35f,
            progressText = "Hedefe %35 yaklaştın",
            icon = androidx.compose.material.icons.Icons.Filled.CardGiftcard,
            modifier = Modifier.fillMaxWidth().padding(AppSpacing.m)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun MilestoneCardPreview() {
    SmokeFreeTheme {
        MilestoneCard(
            title = "24 saat",
            description = "Kalp krizi riski azalır.",
            progress = 1f,
            statusText = "Tamamlandı",
            icon = androidx.compose.material.icons.Icons.Filled.Favorite,
            isCompleted = true,
            modifier = Modifier.fillMaxWidth().padding(AppSpacing.m)
        )
    }
}
