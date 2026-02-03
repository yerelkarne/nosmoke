package com.leosoft.smokefree.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.leosoft.smokefree.data.Message
import com.leosoft.smokefree.data.MessageRepository

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
                title = { Text("Telkin Sözleri") },
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
