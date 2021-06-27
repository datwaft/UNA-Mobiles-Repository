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
import org.una.mobile.client.ScheduleWebSocketClient.Event.*
import org.una.mobile.model.Schedule
import java.security.InvalidParameterException

object ScheduleWebSocketClient {
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
            outputEventChannel.send(Output.ViewAllWithDiscount)
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
                    "VIEW_ALL_WITH_DISCOUNT" -> Input.ViewAllWithDiscount(payload = Json.decodeFromJsonElement(
                        json.jsonObject["view"]!!))
                    else -> throw InvalidParameterException("Frame is not an event: $string")
                }
            }
        }

    private fun Event.parse(): JsonElement = when (this) {
        is Input -> when (this) {
            is Input.ViewAllWithDiscount -> buildJsonObject {
                put("action", "UPDATE")
                put("view", Json.encodeToJsonElement(this@parse.payload))
            }
        }
        is Output -> when (this) {
            Output.ViewAllWithDiscount -> buildJsonObject {
                put("action", "VIEW_ALL_WITH_DISCOUNT")
            }
        }
    }

    sealed class Event {
        sealed class Input : Event() {
            data class ViewAllWithDiscount(val payload: List<Schedule>) : Input()
        }

        sealed class Output : Event() {
            object ViewAllWithDiscount : Output()
        }
    }

    private const val CLIENT_PATH = "$WEB_SOCKET_PATH/schedule"
    private const val TAG = "ScheduleWebSocketClient"
}