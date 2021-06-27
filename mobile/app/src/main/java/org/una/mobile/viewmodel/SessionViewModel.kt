package org.una.mobile.viewmodel

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.una.mobile.client.SessionWebSocketClient
import org.una.mobile.client.SessionWebSocketClient.Event.*
import org.una.mobile.model.Session
import org.una.mobile.ui.layout.SnackbarState

class SessionViewModel : ViewModel() {
    private val client = SessionWebSocketClient

    private val _session = MutableLiveData<Session?>(null)
    val session: LiveData<Session?> = _session
    val isLoggedIn: LiveData<Boolean> = session.map { it != null }

    fun logIn(username: String, password: String) {
        viewModelScope.launch {
            client.outputEventChannel.send(Output.LogIn(username, password))
        }
    }

    fun logOut() {
        viewModelScope.launch {
            client.outputEventChannel.send(LogOut)
        }
    }

    init {
        viewModelScope.launch {
            client.inputEventChannel.consumeEach {
                when (it) {
                    is Input.LogIn -> {
                        _session.postValue(it.session)
                        UserViewModel.internalChannel.send(UserViewModel.InternalEvent.Get(it.session.token))
                    }
                    is LogOut -> {
                        _session.postValue(null)
                        UserViewModel.internalChannel.send(UserViewModel.InternalEvent.Clear)
                    }
                    is Error -> eventChannel.send(Event.Error(it.message))
                    else -> Unit
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            internalChannel.consumeEach {
                when (it) {
                    is InternalEvent.Clear -> {
                        _session.postValue(null)
                        UserViewModel.internalChannel.send(UserViewModel.InternalEvent.Clear)
                    }
                }
            }
        }
    }

    sealed class Event {
        data class Error(val message: String) : Event()
    }

    sealed class InternalEvent {
        object Clear : InternalEvent()
    }

    companion object {
        val eventChannel: Channel<Event> = Channel(10)
        val internalChannel: Channel<InternalEvent> = Channel(10)
    }
}

@Composable
fun SessionListener(
    scaffoldState: ScaffoldState,
) {
    LaunchedEffect(scaffoldState.snackbarHostState) {
        SessionViewModel.eventChannel.consumeEach {
            when (it) {
                is SessionViewModel.Event.Error -> scaffoldState.snackbarHostState.showSnackbar(
                    Json.encodeToString(SnackbarState(SnackbarState.Type.ERROR, it.message))
                )
            }
        }
    }
}