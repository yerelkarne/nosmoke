package com.leosoft.smokefree.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 5.dp)
                )
            }
        },
        windowInsets = WindowInsets(0),
        modifier = Modifier.height(50.dp),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun StatCard(
    iconRes: Int,
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
                painter = painterResource(iconRes),
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
    iconRes: Int,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "badgeProgress")
    Card(
        modifier = modifier,
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.m),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.s)
        ) {
            val alpha = if (isUnlocked) 1f else 0.55f
            Box {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(40.dp).alpha(alpha),
                    tint = if (isUnlocked) Color.Unspecified else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isUnlocked) {
                    Icon(
                        painter = painterResource(android.R.drawable.checkbox_on_background),
                        contentDescription = "Unlocked",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.align(Alignment.TopEnd).size(16.dp)
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
                Text(
                    text = "Kilitli",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
    iconRes: Int,
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
                painter = painterResource(iconRes),
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
    iconRes: Int,
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
                    painter = painterResource(iconRes),
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
            iconRes = com.leosoft.smokefree.R.drawable.health_timer,
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
            iconRes = com.leosoft.smokefree.R.drawable.badge_days_7,
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
            iconRes = com.leosoft.smokefree.R.drawable.reward_shoes,
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
            iconRes = com.leosoft.smokefree.R.drawable.health_heart,
            isCompleted = true,
            modifier = Modifier.fillMaxWidth().padding(AppSpacing.m)
        )
    }
}
