package com.leosoft.smokefree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val questions = listOf(
        "Günde ortalama kaç sigara içiyordun?",
        "Seni en çok motive eden şey nedir?",
        "Bugün kendine nasıl destek olmak istersin?"
    )
    var index by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Başlangıç Soruları") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Soru ${index + 1}/${questions.size}", style = MaterialTheme.typography.labelMedium)
                    Text(text = questions[index], style = MaterialTheme.typography.titleMedium)
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (index < questions.lastIndex) {
                        index += 1
                    } else {
                        onComplete()
                    }
                }
            ) {
                Text(text = if (index < questions.lastIndex) "Devam" else "Başla")
            }
        }
    }
}
