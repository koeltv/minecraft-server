package com.koeltv.plugins

import com.koeltv.routing.configureMinecraftRoutes
import com.koeltv.routing.configureSSERoutes
import com.koeltv.routing.configureSecurityRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

const val MAIN_PATH = "/minecraft"

fun Application.configureRouting() {
    routing {
        staticResources("/files", "files")

        configureSecurityRoutes()

        authenticate("auth-session") {
            configureSSERoutes()
            configureMinecraftRoutes()
        }
    }
}
