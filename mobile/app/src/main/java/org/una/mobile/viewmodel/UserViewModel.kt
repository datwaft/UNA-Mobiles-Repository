package org.una.mobile.viewmodel

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.una.mobile.R
import org.una.mobile.client.UserWebSocketClient
import org.una.mobile.client.UserWebSocketClient.Event.*
import org.una.mobile.model.User
import org.una.mobile.ui.layout.SnackbarState
import org.una.mobile.ui.navigation.ApplicationScreen
import org.una.mobile.ui.navigation.switch

class UserViewModel : ViewModel() {
    private val client = UserWebSocketClient

    private val _user = MutableLiveData<User?>(null)
    val user: LiveData<User?> = _user

    fun get(
        token: String,
    ) {
        viewModelScope.launch {
            client.outputEventChannel.send(Output.Get(token))
        }
    }

    fun register(
        username: String,
        password: String,
        name: String,
        lastname: String,
        email: String,
        address: String,
        workphone: String,
        mobilephone: String,
    ) {
        viewModelScope.launch {
            client.outputEventChannel.send(Output.Register(username,
                password,
                name,
                lastname,
                email,
                address,
                workphone,
                mobilephone))
        }
    }

    fun update(
        token: String,
        name: String,
        lastname: String,
        email: String,
        address: String,
        workphone: String,
        mobilephone: String,
    ) {
        viewModelScope.launch {
            client.outputEventChannel.send(Output.Update(token,
                name,
                lastname,
                email,
                address,
                workphone,
                mobilephone))
        }
    }

    init {
        viewModelScope.launch {
            client.inputEventChannel.consumeEach {
                when (it) {
                    is Input.Register -> eventChannel.send(Event.Register)
                    is Input.Update -> eventChannel.send(Event.Update)
                    is Input.Get -> _user.postValue(it.payload)
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
                    is InternalEvent.Get -> get(it.token)
                    is InternalEvent.Clear -> _user.postValue(null)
                }
            }
        }
    }

    sealed class Event {
        data class Error(val message: String) : Event()
        object Update : Event()
        object Register : Event()
    }

    sealed class InternalEvent {
        data class Get(val token: String) : InternalEvent()
        object Clear : InternalEvent()
    }

    companion object {
        val eventChannel: Channel<Event> = Channel(10)
        val internalChannel: Channel<InternalEvent> = Channel(10)
    }
}

@Composable
fun UserListener(
    scaffoldState: ScaffoldState,
    navController: NavController = rememberNavController(),
) {
    val registeredMessage = stringResource(R.string.event_user_register_message)
    val updatedMessage = stringResource(R.string.event_user_update_message)
    LaunchedEffect(scaffoldState.snackbarHostState) {
        UserViewModel.eventChannel.consumeEach {
            when (it) {
                is UserViewModel.Event.Error -> scaffoldState.snackbarHostState.showSnackbar(
                    Json.encodeToString(SnackbarState(SnackbarState.Type.ERROR, it.message))
                )
                UserViewModel.Event.Register -> {
                    navController.switch(ApplicationScreen.Session)
                    scaffoldState.snackbarHostState.showSnackbar(
                        Json.encodeToString(SnackbarState(SnackbarState.Type.INFORMATION,
                            registeredMessage))
                    )
                }
                UserViewModel.Event.Update -> scaffoldState.snackbarHostState.showSnackbar(
                    Json.encodeToString(SnackbarState(SnackbarState.Type.INFORMATION,
                        updatedMessage))
                )
            }
        }
    }
}