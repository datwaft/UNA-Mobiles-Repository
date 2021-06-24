package org.una.mobile.client

import android.util.Log
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.una.mobile.WEB_SOCKET_URL

object WebSocketClient {
    private var client: HttpClient? = null
    val inputEventChannel: Channel<String> = Channel(10)
    val outputEventChannel: Channel<String> = Channel(10)

    fun connect(coroutineScope: CoroutineScope) {
        Log.d(TAG, "onConnect")
        client = buildClient()
        coroutineScope.launch {
            client?.webSocket(
                method = HttpMethod.Get,
                host = WEB_SOCKET_URL,
//                port = WEB_SOCKET_PORT,
//                path = "/"
            ) {
                val output = launch { output() }
                val input = launch { input() }
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

    private fun Frame.Text.process() = readText()

    private suspend fun DefaultClientWebSocketSession.output() {
        try {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val message = frame.process()
                Log.d(TAG, "Message received: $message")
                outputEventChannel.send(message)
            }
        } catch (e: Throwable) {
            Log.e(TAG, "${e.message}", e)
        }
    }

    private suspend fun DefaultClientWebSocketSession.input() {
        try {
            inputEventChannel.consumeEach { message ->
                Log.d(TAG, "Message sent: $message")
                send(message)
            }
        } catch (e: Throwable) {
            Log.e(TAG, "${e.message}", e)
        }
    }

    private fun buildClient(): HttpClient =
        HttpClient {
            install(WebSockets)
        }

    private const val TAG = "WebSocketClient"
}