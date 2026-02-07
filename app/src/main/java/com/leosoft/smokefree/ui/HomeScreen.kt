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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.leosoft.smokefree.R
import com.leosoft.smokefree.ads.BannerAd
import com.leosoft.smokefree.AppContainer
import com.leosoft.smokefree.data.SettingsDataStore
import com.leosoft.smokefree.notifications.AlarmScheduler
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onMessagesClick: () -> Unit) {
    val context = LocalContext.current
    val settingsStore = remember { SettingsDataStore(context) }
    val achievementEngine = remember { AppContainer.achievementEngine(context) }
    val settings by settingsStore.settingsFlow.collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var count by remember { mutableStateOf(5) }
    var startMinutes by remember { mutableStateOf(8 * 60) }
    var endMinutes by remember { mutableStateOf(20 * 60) }
    var dropdownExpanded by remember { mutableStateOf(false) }
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
        topBar = {
            TopAppBar(
                title = { Text("Sigarasız Koç") },
                actions = {
                    IconButton(onClick = onMessagesClick) {
                        Icon(painterResource(id = R.drawable.ic_message), contentDescription = "Sözler")
                    }
                },
                scrollBehavior = androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Günlük telkin ayarları",
                style = MaterialTheme.typography.titleMedium
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Günlük bildirim sayısı")
                    TextButton(onClick = { dropdownExpanded = true }) {
                        Text(text = "$count adet")
                    }
                    DropdownMenu(expanded = dropdownExpanded, onDismissRequest = { dropdownExpanded = false }) {
                        (3..12).forEach { value ->
                            DropdownMenuItem(
                                text = { Text("$value adet") },
                                onClick = {
                                    count = value
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }

                    TimePickerRow(
                        label = "Başlangıç saati",
                        minutes = startMinutes,
                        onClick = {
                            showTimePicker(context, startMinutes) { startMinutes = it }
                        }
                    )

                    TimePickerRow(
                        label = "Bitiş saati",
                        minutes = endMinutes,
                        onClick = {
                            showTimePicker(context, endMinutes) { endMinutes = it }
                        }
                    )
                }
            }

            Text(
                text = "Sigara içme sıklığın fazlaysa bildirim sayısını daha yüksek seçmek etkili olabilir.",
                style = MaterialTheme.typography.bodyMedium
            )

            errorMessage?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Dakik bildirimler için kesin alarm izni gerekir.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = {
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
                Button(onClick = {
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }) {
                    Text("Bildirim izni ver")
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (endMinutes <= startMinutes) {
                        errorMessage = "Bitiş saati başlangıçtan sonra olmalı."
                        return@Button
                    }
                    errorMessage = null
                    coroutineScope.launch {
                        // Ayarları kaydet ve bugünün bildirimlerini yeniden planla.
                        settingsStore.updateSettings(count, startMinutes, endMinutes)
                        AlarmScheduler(context).scheduleToday(count, startMinutes, endMinutes)
                        achievementEngine.evaluate()
                        snackbarHostState.showSnackbar("Bildirimler planlandı")
                    }
                }
            ) {
                Text("Kaydet")
            }

            Spacer(modifier = Modifier.weight(1f))

            BannerAd(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun TimePickerRow(label: String, minutes: Int, onClick: () -> Unit) {
    val timeText = String.format("%02d:%02d", minutes / 60, minutes % 60)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label)
        Button(onClick = onClick) {
            Text(text = timeText)
        }
    }
}

private fun showTimePicker(context: Context, minutes: Int, onSelected: (Int) -> Unit) {
    val hour = minutes / 60
    val minute = minutes % 60
    TimePickerDialog(context, { _, selectedHour, selectedMinute ->
        onSelected(selectedHour * 60 + selectedMinute)
    }, hour, minute, true).show()
}
