package org.una.mobile.client

import android.util.Log
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import org.una.mobile.WEB_SOCKET_PATH
import org.una.mobile.client.SessionWebSocketClient.Event.*
import org.una.mobile.model.Session
import java.security.InvalidParameterException

object SessionWebSocketClient {
    private var client: HttpClient? = null
    val outputEventChannel: Channel<Event> = Channel(10)
    val inputEventChannel: Channel<Event> = Channel(10)

    fun connect(coroutineScope: CoroutineScope) {
        Log.d(TAG, "onConnect")
        client = buildClient()
        coroutineScope.launch {
            client?.webSocket(path = CLIENT_PATH) {
                val input = launch { output() }
                val output = launch { input() }
                output.join()
                input.join()
            }
            client?.close()
        }
    }

    fun close() {
        Log.d(TAG, "onClose")
        client?.close()
        client = null
    }

    private suspend fun DefaultClientWebSocketSession.input() {
        try {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                try {
                    Log.d(TAG, "Message received: ${frame.readText()}")
                    frame.parse().let { event ->
                        inputEventChannel.send(event)
                    }
                } catch (e: Throwable) {
                    Log.e(TAG, "${e.message}", e)
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "${e.message}", e)
        }
    }

    private suspend fun DefaultClientWebSocketSession.output() {
        try {
            outputEventChannel.consumeEach { event ->
                event.parse().let {
                    Log.d(TAG, "Event sent: $it")
                    send(it.toString())
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "${e.message}", e)
        }
    }

    private suspend fun Frame.Text.parse(): Event =
        readText().let { string ->
            Json.parseToJsonElement(string).let { json ->
                when (json.jsonObject["action"]?.jsonPrimitive?.contentOrNull) {
                    "LOGIN" -> when (json.jsonObject["authorization"]?.jsonPrimitive?.contentOrNull) {
                        "user" -> Input.LogIn(Json.decodeFromJsonElement(json))
                        else -> {
                            outputEventChannel.send(LogOut)
                            Error.Credentials
                        }
                    }
                    "LOGOUT" -> LogOut
                    "ERROR" -> when (json.jsonObject["type"]?.jsonPrimitive?.contentOrNull) {
                        "CREDENTIALS" -> Error.Credentials
                        else -> throw InvalidParameterException("Invalid error type: $string")
                    }
                    else -> throw InvalidParameterException("Frame is not an event: $string")
                }
            }
        }

    private fun Event.parse(): JsonElement = when (this) {
        is Input -> when (this) {
            is Input.LogIn -> buildJsonObject {
                put("action", "LOGIN")
                put("username", this@parse.session.username)
                put("token", this@parse.session.token)
                put("authorization", "user")
            }
        }
        is Output -> when (this) {
            is Output.LogIn -> buildJsonObject {
                put("action", "LOGIN")
                put("username", this@parse.username)
                put("password", this@parse.password)
            }
        }
        LogOut -> buildJsonObject { put("action", "LOGOUT") }
        is Error -> buildJsonObject {
            put("action", "ERROR")
            when (this@parse) {
                Error.Credentials -> put("type", "CREDENTIALS")
            }
        }
    }

    sealed class Event {
        sealed class Input : Event() {
            data class LogIn(val session: Session) : Input()
        }

        sealed class Output : Event() {
            data class LogIn(val username: String, val password: String) : Output()
        }

        object LogOut : Event()

        sealed class Error(val message: String) : Event() {
            object Credentials : Error(message = "Invalid credentials")
        }
    }

    private const val CLIENT_PATH = "$WEB_SOCKET_PATH/session"
    private const val TAG = "SessionWebSocketClient"
}