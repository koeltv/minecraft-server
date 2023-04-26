package com.koeltv.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.channels.produce

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
 * Method that responds an [ApplicationCall] by reading all the [ServerSendEvent]s from the specified [events] [ReceiveChannel]
 * and serializing them in a way that is compatible with the Server-Sent Events specification.
 *
 * You can read more about it here: https://www.html5rocks.com/en/tutorials/eventsource/basics/
 */
suspend fun ApplicationCall.respondSSE(events: ReceiveChannel<ServerSendEvent>) {
    response.cacheControl(CacheControl.NoCache(null))
    respondTextWriter(contentType = ContentType.Text.EventStream) {
        withContext(Dispatchers.IO) {
            for (event in events) {
                write(event.asString())
                flush()
            }
        }
    }
}

@OptIn(ObsoleteCoroutinesApi::class, ExperimentalCoroutinesApi::class)
fun Application.configureSSE() {
    /**
     * We produce a [BroadcastChannel] from a suspending function
     * that send a [ServerSendEvent] instance each second.
     */
    val channel: BroadcastChannel<ServerSendEvent> = produce {
        var n = 0
        while (true) {
            send(ServerSendEvent("demo$n"))
            delay(1000)
            n++
        }
    }.broadcast()

    /**
     * We use the [Routing] plugin to declare [Route] that will be
     * executed per call
     */
    routing {
        route("sse") {
            get {
                call.respond(FreeMarkerContent("sse.ftl", null))
            }

            /**
             * Route to be executed when the client perform a GET `/sse` request.
             * It will respond using the [respondSSE] extension method defined in this same file
             * that uses the [BroadcastChannel] channel we created earlier to emit those events.
             */
            get("/events") {
                application.log.info("New subscription !")
                val events = channel.openSubscription()
                try {
                    call.respondSSE(events)
                } finally {
                    events.cancel()
                }
            }
        }
    }
}