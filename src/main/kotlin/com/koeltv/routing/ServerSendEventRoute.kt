package com.koeltv.routing

import com.koeltv.plugins.eventFlow
import com.koeltv.plugins.respondSSE
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take

fun Route.configureSSERoutes() {
    route("sse") {
        get {
            call.respond(FreeMarkerContent("sse.ftl", null))
        }

        /**
         * Route to be executed when the client perform a GET `/sse` request.
         * It will respond using the [respondSSE] extension method defined in this same file
         * that uses the [Flow] we created earlier to emit those events.
         */
        get("/events") {
            application.log.info("New subscription !")
            try {
                call.respondSSE(eventFlow)
            } finally {
                eventFlow.take(1)
            }
        }
    }
}