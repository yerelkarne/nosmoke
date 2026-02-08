package com.leosoft.smokefree.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.LaunchedEffect
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(onBack: () -> Unit, onMessageClick: (Int) -> Unit) {
    val context = LocalContext.current
    val messagesState = remember { mutableStateOf<List<Message>>(emptyList()) }

    LaunchedEffect(Unit) {
        messagesState.value = MessageRepository.loadMessages(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Telkin Sözleri",
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
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Ayarlar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(messagesState.value) { message ->
                Column(
                    modifier = Modifier
                        .clickable { onMessageClick(message.id) }
                        .padding(16.dp)
                ) {
                    Text(text = message.text, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
