package com.koeltv

import com.koeltv.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

val currentOs by lazy {
    System.getProperty("os.name").lowercase()
}

@Suppress("unused")
fun Application.module() {
    configureSecurity()
    configureStatusPages()
    configureTemplating()
    configureRouting()
}
