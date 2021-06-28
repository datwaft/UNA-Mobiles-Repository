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
import org.una.mobile.client.TicketWebSocketClient
import org.una.mobile.client.TicketWebSocketClient.Event.Input
import org.una.mobile.client.TicketWebSocketClient.Event.Output
import org.una.mobile.model.Ticket
import org.una.mobile.ui.layout.SnackbarState
import org.una.mobile.viewmodel.TicketViewModel.Event.Create
import org.una.mobile.viewmodel.TicketViewModel.Event.Error

class TicketViewModel : ViewModel() {
    private val client = TicketWebSocketClient

    private val _flightItems = MutableLiveData<List<Ticket>>(emptyList())
    val flightItems: LiveData<List<Ticket>> = _flightItems

    private val _purchaseItems = MutableLiveData<List<Ticket>>(emptyList())
    val purchaseItems: LiveData<List<Ticket>> = _purchaseItems

    fun viewAllPerFlight(flight: Long) {
        viewModelScope.launch {
            client.outputEventChannel.send(Output.ViewAllPerFlight(flight))
        }
    }

    fun viewAllPerPurchase(purchase: Long) {
        viewModelScope.launch {
            client.outputEventChannel.send(Output.ViewAllPerPurchase(purchase))
        }
    }

    fun create(purchase: Long, flight: Long, items: List<Pair<Int, Int>>) {
        viewModelScope.launch {
            client.outputEventChannel.send(Output.Create(purchase, flight, items))
        }
    }

    init {
        viewModelScope.launch {
            client.inputEventChannel.consumeEach {
                when (it) {
                    is Input.ViewAllPerFlight -> _flightItems.postValue(it.payload)
                    is Input.ViewAllPerPurchase -> _purchaseItems.postValue(it.payload)
                    is Input.Create -> {
                        eventChannel.send(Create)
                        PurchaseViewModel.internalChannel.send(PurchaseViewModel.InternalEvent.ViewAll)
                    }
                    else -> Unit
                }
            }
        }
    }

    sealed class Event {
        data class Error(val message: String) : Event()
        object Create : Event()
    }

    companion object {
        val eventChannel: Channel<Event> = Channel(10)
    }
}

@Composable
fun TicketListener(
    scaffoldState: ScaffoldState,
) {
    val reservationMessage = stringResource(R.string.event_reservation_create_message)
    LaunchedEffect(scaffoldState.snackbarHostState) {
        TicketViewModel.eventChannel.consumeEach {
            when (it) {
                is Error -> scaffoldState.snackbarHostState.showSnackbar(
                    Json.encodeToString(SnackbarState(SnackbarState.Type.ERROR, it.message))
                )
                Create -> scaffoldState.snackbarHostState.showSnackbar(
                    Json.encodeToString(SnackbarState(SnackbarState.Type.INFORMATION,
                        reservationMessage))
                )
            }
        }
    }
}