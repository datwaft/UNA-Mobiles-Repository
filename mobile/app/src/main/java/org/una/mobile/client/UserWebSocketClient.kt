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
import org.una.mobile.client.UserWebSocketClient.Event.*
import org.una.mobile.model.User
import java.security.InvalidParameterException

object UserWebSocketClient {
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

    private fun Frame.Text.parse(): Event =
        readText().let { string ->
            Json.parseToJsonElement(string).let { json ->
                when (json.jsonObject["action"]?.jsonPrimitive?.contentOrNull) {
                    "GET" -> Input.Get(payload = Json.decodeFromJsonElement(json.jsonObject["get"]!!))
                    "REGISTER" -> Input.Register
                    "UPDATE" -> Input.Update
                    "ERROR" -> when (json.jsonObject["type"]?.jsonPrimitive?.contentOrNull) {
                        "CREDENTIALS" -> Error.Credentials
                        "DUPLICATE" -> Error.Duplicate
                        null -> json.jsonObject["message"]?.jsonPrimitive?.contentOrNull.let {
                            when (it) {
                                null -> throw InvalidParameterException("Invalid error: $string")
                                else -> Error.Generic(it)
                            }
                        }
                        else -> throw InvalidParameterException("Invalid error type: $string")
                    }
                    else -> throw InvalidParameterException("Frame is not an event: $string")
                }
            }
        }

    private fun Event.parse(): JsonElement = when (this) {
        is Input -> when (this) {
            is Input.Get -> buildJsonObject {
                put("action", "GET")
                put("get", Json.encodeToJsonElement(this@parse.payload))
            }
            Input.Register -> buildJsonObject { put("action", "REGISTER") }
            Input.Update -> buildJsonObject { put("action", "UPDATE") }
        }
        is Output -> when (this) {
            is Output.Get -> buildJsonObject {
                put("action", "GET")
                put("token", this@parse.token)
            }
            is Output.Register -> buildJsonObject {
                put("action", "REGISTER")
                put("username", this@parse.username)
                put("password", this@parse.password)
                put("name", this@parse.name)
                put("lastname", this@parse.lastname)
                put("email", this@parse.email)
                put("address", this@parse.address)
                put("workphone", this@parse.workphone)
                put("mobilephone", this@parse.mobilephone)
            }
            is Output.Update -> buildJsonObject {
                put("action", "UPDATE")
                put("token", this@parse.token)
                put("name", this@parse.name)
                put("lastname", this@parse.lastname)
                put("email", this@parse.email)
                put("address", this@parse.address)
                put("workphone", this@parse.workphone)
                put("mobilephone", this@parse.mobilephone)
            }
        }
        is Error -> buildJsonObject {
            put("action", "ERROR")
            when (this@parse) {
                is Error.Generic -> put("message", this@parse.message)
                Error.Credentials -> put("type", "CREDENTIALS")
                Error.Duplicate -> put("type", "DUPLICATE")
            }
        }
    }

    sealed class Event {
        sealed class Input : Event() {
            data class Get(val payload: User) : Input()
            object Register : Input()
            object Update : Input()
        }

        sealed class Output : Event() {
            data class Get(val token: String) : Output()

            data class Register(
                val username: String,
                val password: String,
                val name: String,
                val lastname: String,
                val email: String,
                val address: String,
                val workphone: String,
                val mobilephone: String,
            ) : Output()

            data class Update(
                val token: String,
                val name: String,
                val lastname: String,
                val email: String,
                val address: String,
                val workphone: String,
                val mobilephone: String,
            ) : Output()
        }

        sealed class Error(open val message: String) : Event() {
            data class Generic(override val message: String) : Error(message)
            object Credentials : Error(message = "Invalid credentials")
            object Duplicate : Error(message = "User with that username already exists")
        }
    }

    private const val CLIENT_PATH = "$WEB_SOCKET_PATH/user"
    private const val TAG = "UserWebSocketClient"
}