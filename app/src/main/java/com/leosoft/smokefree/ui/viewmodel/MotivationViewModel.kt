package com.leosoft.smokefree.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.leosoft.smokefree.AppContainer
import com.leosoft.smokefree.data.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MotivationViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(MotivationUiState())
    val uiState: StateFlow<MotivationUiState> = _uiState

    init {
        refreshQuote()
    }

    fun refreshQuote() {
        viewModelScope.launch {
            val messages = withContext(Dispatchers.IO) {
                MessageRepository.loadMessages(getApplication())
            }
            val quote = messages.randomOrNull()?.text ?: "Bugün de sigarasız kalmayı seçtin."
            _uiState.value = _uiState.value.copy(quote = quote)
        }
    }
}

data class MotivationUiState(
    val quote: String = ""
)
