package org.una.mobile.client

import android.util.Log
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import org.una.mobile.WEB_SOCKET_PATH
import org.una.mobile.client.TicketWebSocketClient.Event.*
import org.una.mobile.client.TicketWebSocketClient.Event.Error.*
import org.una.mobile.model.Ticket
import java.security.InvalidParameterException

object TicketWebSocketClient {
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
        } catch (e: ClosedReceiveChannelException) {
            Log.d(TAG, "onClose: ${closeReason.await()}")
        } catch (e: Throwable) {
            Log.e(TAG, "onError${closeReason.await()}")
        }
    }

    private fun Frame.Text.parse(): Event =
        readText().let { string ->
            Json.parseToJsonElement(string).let { json ->
                when (json.jsonObject["action"]?.jsonPrimitive?.contentOrNull) {
                    "VIEW_ALL_PER_FLIGHT" -> Input.ViewAllPerFlight(
                        flight = Json.decodeFromJsonElement(json.jsonObject["flight"]!!),
                        payload = Json.decodeFromJsonElement(json.jsonObject["view"]!!))
                    "VIEW_ALL_PER_PURCHASE" -> Input.ViewAllPerPurchase(
                        purchase = Json.decodeFromJsonElement(json.jsonObject["purchase"]!!),
                        payload = Json.decodeFromJsonElement(json.jsonObject["view"]!!))
                    "CREATE" -> Input.Create
                    "ERROR" -> json.jsonObject["message"]?.jsonPrimitive?.contentOrNull.let {
                        when (it) {
                            null -> throw InvalidParameterException("Invalid error: $string")
                            else -> Generic(it)
                        }
                    }
                    else -> throw InvalidParameterException("Frame is not an event: $string")
                }
            }
        }

    private fun Event.parse(): JsonElement = when (this) {
        is Input -> when (this) {
            is Input.ViewAllPerFlight -> buildJsonObject {
                put("action", "VIEW_ALL_PER_FLIGHT")
                put("flight", this@parse.flight)
                put("view", Json.encodeToJsonElement(this@parse.payload))
            }
            is Input.ViewAllPerPurchase -> buildJsonObject {
                put("action", "VIEW_ALL_PER_PURCHASE")
                put("purchase", this@parse.purchase)
                put("view", Json.encodeToJsonElement(this@parse.payload))
            }
            Input.Create -> buildJsonObject { put("action", "CREATE") }
        }
        is Output -> when (this) {
            is Output.Create -> buildJsonObject {
                put("action", "CREATE")
                put("purchase", this@parse.purchase)
                put("flight", this@parse.flight)
                putJsonArray("tickets") {
                    this@parse.payload.forEach { (row, column) ->
                        addJsonObject {
                            put("row", row)
                            put("column", column)
                        }
                    }
                }
            }
            is Output.ViewAllPerFlight -> buildJsonObject {
                put("action", "VIEW_ALL_PER_FLIGHT")
                put("flight", this@parse.flight)
            }
            is Output.ViewAllPerPurchase -> buildJsonObject {
                put("action", "VIEW_ALL_PER_PURCHASE")
                put("purchase", this@parse.purchase)
            }
        }
        is Error -> buildJsonObject {
            put("action", "ERROR")
            when (this@parse) {
                is Generic -> put("message", this@parse.message)
            }
        }
    }

    sealed class Event {
        sealed class Input : Event() {
            data class ViewAllPerFlight(val flight: Long, val payload: List<Ticket>) : Input()
            data class ViewAllPerPurchase(val purchase: Long, val payload: List<Ticket>) : Input()
            object Create : Input()
        }

        sealed class Output : Event() {
            data class ViewAllPerFlight(val flight: Long) : Output()
            data class ViewAllPerPurchase(val purchase: Long) : Output()
            data class Create(val purchase: Long, val flight: Long, val payload: List<Pair<Int, Int>>) : Output()
        }

        sealed class Error(open val message: String) : Event() {
            data class Generic(override val message: String) : Error(message)
        }
    }

    private const val CLIENT_PATH = "$WEB_SOCKET_PATH/ticket"
    private const val TAG = "TicketWebSocketClient"
}