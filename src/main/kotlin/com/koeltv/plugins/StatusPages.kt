package com.koeltv.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

const val DEFAULT_MESSAGE = "Oups, this seems bad, please report this error"

fun Application.configureStatusPages() {
    install(StatusPages) {
        unhandled { call ->
            call.respondStatusPage(HttpStatusCode.InternalServerError, DEFAULT_MESSAGE)
        }
    }
}

private suspend fun ApplicationCall.respondStatusPage(statusCode: HttpStatusCode, message: String?) {
    respond(
        statusCode,
        FreeMarkerContent("status.ftl", mapOf("message" to (message ?: DEFAULT_MESSAGE)))
    )
}