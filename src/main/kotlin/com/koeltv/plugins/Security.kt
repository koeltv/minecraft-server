package com.koeltv.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*
import java.security.MessageDigest

private val adminName: String = System.getenv("USER")
private val password: String = System.getenv("ROOT_PASSWORD")

data class UserSession(val hash: String) : Principal

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<UserSession>("MY_SESSION") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 600
        }
    }
    install(Authentication) {
        form("auth-form") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                credentials
                    .takeIf { it.name == adminName && it.password == password }
                    ?.let { UserIdPrincipal(it.name.sha256() + it.password.sha256()) }
            }
        }
        session<UserSession>("auth-session") {
            validate {
                it.takeIf { it.hash == adminName.sha256() + password.sha256() }
            }
            challenge("/login")
        }
    }
}

/**
 * Takes a string and return its hash in SHA-256.
 */
private fun String.sha256(): String {
    val bytes = MessageDigest.getInstance("SHA-256")
        .digest(this.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
