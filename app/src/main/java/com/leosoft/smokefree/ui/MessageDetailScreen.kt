package com.leosoft.smokefree.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.leosoft.smokefree.data.Message
import com.leosoft.smokefree.data.MessageRepository
import com.leosoft.smokefree.data.SettingsDataStore
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun MessageDetailScreen(messageId: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val settingsStore = remember { SettingsDataStore(context) }
    val settings by settingsStore.settingsFlow.collectAsState(initial = null)
    val messageState = remember { mutableStateOf<Message?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messageId) {
        messageState.value = MessageRepository.findMessage(context, messageId)
    }

    val message = messageState.value
    val isFavorite = settings?.favorites?.contains(messageId.toString()) == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Telkin Detayı") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_revert),
                            contentDescription = "Geri"
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
}
