package org.una.mobile.viewmodel

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.una.mobile.R
import org.una.mobile.client.PurchaseWebSocketClient
import org.una.mobile.client.PurchaseWebSocketClient.Event.Input
import org.una.mobile.client.PurchaseWebSocketClient.Event.Output
import org.una.mobile.model.Purchase
import org.una.mobile.ui.layout.SnackbarState
import org.una.mobile.viewmodel.PurchaseViewModel.Event.Create
import org.una.mobile.viewmodel.PurchaseViewModel.Event.Error

class PurchaseViewModel : ViewModel() {
    private val client = PurchaseWebSocketClient

    private val _items = MutableLiveData<List<Purchase>>(emptyList())
    val items: LiveData<List<Purchase>> = _items

    var token: String? = null

    fun viewAll(token: String) {
        viewModelScope.launch {
            client.outputEventChannel.send(Output.ViewAll(token))
        }
    }

    fun viewAll() {
        token ?: return
        viewModelScope.launch {
            client.outputEventChannel.send(Output.ViewAll(token!!))
        }
    }

    fun create(ticketNumber: Int, flight: Long) {
        token ?: return
        viewModelScope.launch {
            client.outputEventChannel.send(Output.Create(token!!, ticketNumber, flight))
        }
    }

    init {
        viewModelScope.launch {
            client.inputEventChannel.consumeEach {
                when (it) {
                    is Input.ViewAll -> _items.postValue(it.payload)
                    is Input.Create -> {
                        eventChannel.send(Create)
                        FlightViewModel.internalChannel.send(FlightViewModel.InternalEvent.ViewAll)
                    }
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

    sealed class Event {
        data class Error(val message: String) : Event()
        object Create : Event()
    }

    sealed class InternalEvent {
        object ViewAll : InternalEvent()
    }

    companion object {
        val eventChannel: Channel<Event> = Channel(10)
        val internalChannel: Channel<InternalEvent> = Channel(10)
    }
}

@Composable
fun PurchaseListener(
    scaffoldState: ScaffoldState,
) {
    val createdMessage = stringResource(R.string.event_purchase_create_message)
    LaunchedEffect(scaffoldState.snackbarHostState) {
        PurchaseViewModel.eventChannel.consumeEach {
            when (it) {
                is Error -> scaffoldState.snackbarHostState.showSnackbar(
                    Json.encodeToString(SnackbarState(SnackbarState.Type.ERROR, it.message))
                )
                Create -> scaffoldState.snackbarHostState.showSnackbar(
                    Json.encodeToString(SnackbarState(SnackbarState.Type.INFORMATION,
                        createdMessage))
                )
            }
        }
    }
}