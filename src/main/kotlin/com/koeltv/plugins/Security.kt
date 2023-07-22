package com.koeltv.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*
import io.ktor.util.*

private val adminName: String = System.getenv("USERNAME") ?: "admin"
private val password: String = System.getenv("ROOT_PASSWORD") ?: "admin"

private val secretEncryptKey = hex(System.getenv("SECRET_ENCRYPT_KEY") ?: "00112233445566778899aabbccddeeff")
private val secretSignKey = hex(System.getenv("SECRET_SIGN_KEY") ?: "6819b57a326945c1968f45236589")

data class UserSession(val hash: String) : Principal

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<UserSession>("MY_SESSION") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 600
            cookie.extensions["SameSite"] = "strict"
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }
    install(Authentication) {
        form("auth-form") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                credentials
                    .takeIf { it.name == adminName && it.password == password }
                    ?.let { UserIdPrincipal(it.name + it.password) }
            }
            challenge("/login")
        }
        session<UserSession>("auth-session") {
            validate {
                it.takeIf { it.hash == adminName + password }
            }
            challenge("/login")
        }
    }
}
