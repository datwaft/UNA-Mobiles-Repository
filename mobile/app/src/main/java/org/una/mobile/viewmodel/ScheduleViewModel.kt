package org.una.mobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.una.mobile.client.ScheduleWebSocketClient
import org.una.mobile.client.ScheduleWebSocketClient.Event.Input
import org.una.mobile.client.ScheduleWebSocketClient.Event.Output
import org.una.mobile.model.Schedule

class ScheduleViewModel : ViewModel() {
    private val client = ScheduleWebSocketClient

    private val _items = MutableLiveData<List<Schedule>>(emptyList())
    val items: LiveData<List<Schedule>> = _items

    fun viewAllWithDiscount() {
        viewModelScope.launch {
            client.outputEventChannel.send(Output.ViewAllWithDiscount)
        }
    }

    init {
        viewModelScope.launch {
            client.inputEventChannel.consumeEach {
                when (it) {
                    is Input.ViewAllWithDiscount -> _items.postValue(it.payload)
                    else -> Unit
                }
            }
        }
    }
}