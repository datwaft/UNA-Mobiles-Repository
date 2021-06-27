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
import org.una.mobile.client.FlightWebSocketClient.Event.*
import org.una.mobile.model.Flight
import java.security.InvalidParameterException

object FlightWebSocketClient {
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
        coroutineScope.launch {
            outputEventChannel.send(Output.ViewAll)
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
                    "VIEW_ALL" -> Input.ViewAll(payload = Json.decodeFromJsonElement(json.jsonObject["view"]!!))
                    else -> throw InvalidParameterException("Frame is not an event: $string")
                }
            }
        }

    private fun Event.parse(): JsonElement = when (this) {
        is Input -> when (this) {
            is Input.ViewAll -> buildJsonObject {
                put("action", "VIEW_ALL")
                put("view", Json.encodeToJsonElement(this@parse.payload))
            }
        }
        is Output -> when (this) {
            Output.ViewAll -> buildJsonObject {
                put("action", "VIEW_ALL")
            }
        }
    }

    sealed class Event {
        sealed class Input : Event() {
            data class ViewAll(val payload: List<Flight>) : Input()
        }

        sealed class Output : Event() {
            object ViewAll : Output()
        }
    }

    private const val CLIENT_PATH = "$WEB_SOCKET_PATH/flight"
    private const val TAG = "FlightWebSocketClient"
}