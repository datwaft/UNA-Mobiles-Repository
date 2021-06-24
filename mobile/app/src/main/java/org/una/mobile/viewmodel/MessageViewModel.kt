package org.una.mobile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.una.mobile.client.WebSocketClient

class MessageViewModel : ViewModel() {
    var items: List<String> by mutableStateOf(emptyList())
        private set

    init {
        viewModelScope.launch {
            WebSocketClient.outputEventChannel.consumeEach { item ->
                items += item
            }
        }
    }

    fun send(item: String) {
        viewModelScope.launch {
            WebSocketClient.inputEventChannel.send(item)
        }
    }
}