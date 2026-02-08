package com.leosoft.smokefree.ui

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.leosoft.smokefree.R
import com.leosoft.smokefree.data.Message
import com.leosoft.smokefree.data.MessageRepository
import com.leosoft.smokefree.data.SettingsDataStore
import com.leosoft.smokefree.ui.viewmodel.ProgressViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailScreen(messageId: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val settingsStore = remember { SettingsDataStore(context) }
    val settings by settingsStore.settingsFlow.collectAsState(initial = null)
    val messageState = remember { mutableStateOf<Message?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val progressViewModel: ProgressViewModel = viewModel()
    val progressState by progressViewModel.uiState.collectAsState()
    val showSettingsDialog = remember { mutableStateOf(false) }

    LaunchedEffect(messageId) {
        messageState.value = MessageRepository.findMessage(context, messageId)
    }

    val message = messageState.value
    val isFavorite = settings?.favorites?.contains(messageId.toString()) == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Telkin Detayı",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .padding(top = 15.dp)
                        )
                    }
                },
                windowInsets = WindowInsets(0),
                modifier = Modifier.height(50.dp),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Geri"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsDialog.value = true }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Ayarlar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = message?.text ?: "Mesaj bulunamadı.",
                style = MaterialTheme.typography.bodyLarge
            )

            Button(onClick = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message?.text ?: "")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Paylaş"))
            }) {
                Text("Paylaş")
            }

            Button(onClick = {
                coroutineScope.launch {
                    settingsStore.toggleFavorite(messageId)
                }
            }) {
                Text(if (isFavorite) "Favoriden çıkar" else "Favoriye ekle")
            }
        }
    }

    SettingsDialog(
        show = showSettingsDialog.value,
        cigarettesPerDay = progressState.cigarettesPerDay,
        packPrice = progressState.packPrice,
        packSize = progressState.packSize,
        smokeFreeDays = progressState.smokeFreeDays,
        onDismiss = { showSettingsDialog.value = false },
        onSave = { startTimestamp, cigarettes, price, size ->
            progressViewModel.updateUserStats(startTimestamp, cigarettes, price, size)
            showSettingsDialog.value = false
        }
    )
}
