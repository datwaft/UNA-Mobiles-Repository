package org.una.mobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.una.mobile.client.FlightWebSocketClient
import org.una.mobile.client.FlightWebSocketClient.Event.Input
import org.una.mobile.client.FlightWebSocketClient.Event.Output
import org.una.mobile.model.Flight

class FlightViewModel : ViewModel() {
    private val client = FlightWebSocketClient

    private val _items = MutableLiveData<List<Flight>>(emptyList())
    val items: LiveData<List<Flight>> = _items

    fun viewAll() {
        viewModelScope.launch {
            client.outputEventChannel.send(Output.ViewAll)
        }
    }

    init {
        viewModelScope.launch {
            client.inputEventChannel.consumeEach {
                when (it) {
                    is Input.ViewAll -> _items.postValue(it.payload)
                    else -> Unit
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            internalChannel.consumeEach {
                when (it) {
                    is InternalEvent.ViewAll -> viewAll()
                }
            }
        }
    }

    sealed class InternalEvent {
        object ViewAll : InternalEvent()
    }

    companion object {
        val internalChannel: Channel<InternalEvent> = Channel(10)
    }
}