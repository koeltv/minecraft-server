package com.koeltv.routing

import com.koeltv.plugins.MAIN_PATH
import com.koeltv.plugins.UserSession
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.configureSecurityRoutes() {
    get("/") {
        call.respondRedirect("/login")
    }

    get("/login") {
        call.respond(FreeMarkerContent("login.ftl", null))
    }

    //Here we get the form authentification and create a session
    authenticate("auth-form") {
        post("/login") {
            val userName = call.principal<UserIdPrincipal>()?.name.toString()
            call.sessions.set(UserSession(hash = userName))
            call.respondRedirect(MAIN_PATH)
        }
    }

    //Here we log out the user
    get("/logout") {
        call.sessions.clear<UserSession>()
        call.respondRedirect("/login")
    }
}