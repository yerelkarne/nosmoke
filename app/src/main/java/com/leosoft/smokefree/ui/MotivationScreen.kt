package com.leosoft.smokefree.ui

import android.Manifest
import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leosoft.smokefree.data.SettingsDataStore
import com.leosoft.smokefree.notifications.AlarmScheduler
import com.leosoft.smokefree.ui.viewmodel.MotivationViewModel
import com.leosoft.smokefree.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotivationScreen(onMessagesClick: () -> Unit) {
    val context = LocalContext.current
    val viewModel: MotivationViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val settingsStore = remember { SettingsDataStore(context) }
    val settings by settingsStore.settingsFlow.collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var count by remember { mutableStateOf(5) }
    var startMinutes by remember { mutableStateOf(8 * 60) }
    var endMinutes by remember { mutableStateOf(20 * 60) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(settings) {
        settings?.let {
            count = it.dailyCount
            startMinutes = it.startMinutes
            endMinutes = it.endMinutes
        }
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    val alarmManager = context.getSystemService(AlarmManager::class.java)

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.title_motivation)) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppSpacing.m),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.m)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(AppSpacing.m), verticalArrangement = Arrangement.spacedBy(AppSpacing.s)) {
                    Text(text = stringResource(R.string.label_daily_quote), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.s)) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(48.dp)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                        Text(
                            text = uiState.quote,
                            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                            maxLines = 4,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                    TextButton(onClick = onMessagesClick) {
                        Text(text = "Tüm sözleri gör")
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(AppSpacing.m), verticalArrangement = Arrangement.spacedBy(AppSpacing.s)) {
                    Text(text = stringResource(R.string.label_notifications), style = MaterialTheme.typography.titleLarge)
                    Text(text = "Günlük bildirim sayısı: $count", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Slider(
                        value = count.toFloat(),
                        onValueChange = { count = it.toInt() },
                        valueRange = 3f..12f,
                        steps = 8
                    )
                    TimePickerRow(label = "Başlangıç", minutes = startMinutes) {
                        showTimePicker(context, startMinutes) { startMinutes = it }
                    }
                    TimePickerRow(label = "Bitiş", minutes = endMinutes) {
                        showTimePicker(context, endMinutes) { endMinutes = it }
                    }
                    Text(
                        text = "Sigara içme sıklığın fazlaysa bildirim sayısını daha yüksek seçmek etkili olabilir.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            errorMessage?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(AppSpacing.m), verticalArrangement = Arrangement.spacedBy(AppSpacing.s)) {
                        Text(text = "Dakik bildirimler için kesin alarm izni gerekir.")
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            onClick = {
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                            context.startActivity(intent)
                        }) {
                            Text("İzni Aç")
                        }
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    onClick = { notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
                ) {
                    Text("Bildirim izni ver")
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                onClick = {
                    if (endMinutes <= startMinutes) {
                        errorMessage = "Bitiş saati başlangıçtan sonra olmalı."
                        return@Button
                    }
                    errorMessage = null
                    coroutineScope.launch {
                        settingsStore.updateSettings(count, startMinutes, endMinutes)
                        AlarmScheduler(context).scheduleToday(count, startMinutes, endMinutes)
                        snackbarHostState.showSnackbar("Bildirimler planlandı")
                    }
                }
            ) {
                Text(stringResource(R.string.label_save))
            }
        }
    }
}

@Composable
private fun TimePickerRow(label: String, minutes: Int, onClick: () -> Unit) {
    val timeText = String.format("%02d:%02d", minutes / 60, minutes % 60)
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        TextButton(onClick = onClick) { Text(text = timeText) }
    }
}

private fun showTimePicker(context: Context, minutes: Int, onSelected: (Int) -> Unit) {
    val hour = minutes / 60
    val minute = minutes % 60
    TimePickerDialog(context, { _, selectedHour, selectedMinute ->
        onSelected(selectedHour * 60 + selectedMinute)
    }, hour, minute, true).show()
}

@Preview(showBackground = true)
@Composable
private fun MotivationPreview() {
    SmokeFreeTheme {
        MotivationScreen(onMessagesClick = {})
    }
}
