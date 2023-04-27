package com.koeltv.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

/**
 * The data class representing an SSE Event that will be sent to the client.
 */
data class ServerSendEvent(val data: String, val event: String? = null, val id: String? = null) {
    fun asString(): String {
        val buffer = StringBuffer()

        id?.let { buffer.appendLine("id: $id") }
        event?.let { buffer.appendLine("event: $event") }
        data.lines().forEach { buffer.appendLine("data: $it") }
        buffer.appendLine()

        return buffer.toString()
    }
}

/**
 * Method that responds an [ApplicationCall] by reading all the [ServerSendEvent]s from the specified [events] [Flow]
 * and serializing them in a way that is compatible with the Server-Sent Events specification.
 *
 * You can read more about it here: https://www.html5rocks.com/en/tutorials/eventsource/basics/
 */
suspend fun ApplicationCall.respondSSE(events: Flow<ServerSendEvent>) {
    response.cacheControl(CacheControl.NoCache(null))
    respondTextWriter(contentType = ContentType.Text.EventStream) {
        events.collect {
            withContext(Dispatchers.IO) {
                write(it.asString())
                flush()
            }
        }
    }
}

/**
 * We produce a [Flow] from a suspending function
 * that send a [ServerSendEvent] instance each second.
 */
val eventFlow: MutableSharedFlow<ServerSendEvent> = MutableSharedFlow<ServerSendEvent>().also {
    it.onEach { event -> println("Hey !") }
}