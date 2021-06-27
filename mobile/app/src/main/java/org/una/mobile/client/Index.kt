package org.una.mobile.client

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import org.una.mobile.WEB_SOCKET_PORT
import org.una.mobile.WEB_SOCKET_URL

internal fun buildClient(): HttpClient = HttpClient {
    install(WebSockets)
}

internal suspend fun HttpClient.webSocket(
    path: String,
    block: suspend DefaultClientWebSocketSession.() -> Unit,
) {
    webSocket(
        method = HttpMethod.Get,
        host = WEB_SOCKET_URL,
        port = WEB_SOCKET_PORT,
        path = path,
        block = block,
    )
}

internal val Json = Json { ignoreUnknownKeys = true }