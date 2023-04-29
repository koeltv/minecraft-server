package com.koeltv.routing

import com.koeltv.plugins.ServerSendEvent
import com.koeltv.plugins.eventFlow
import com.koeltv.plugins.respondSSE
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take

fun Route.configureSSERoutes() {
    route("sse") {
        /**
         * Route to be executed when the client perform a GET `/sse` request.
         * It will respond using the [respondSSE] extension method defined in this same file
         * that uses the [Flow] we created earlier to emit those events.
         */
        get("/events") {
            try {
                call.respondSSE(
                    eventFlow.onStart {
                        emit(ServerSendEvent("Starting point", "open"))
                        logFile.useLines { lines ->
                            lines.forEach {
                                emit(ServerSendEvent(it))
                            }
                        }
                    }
                )
            } finally {
                eventFlow.take(1)
            }
        }
    }
}