package com.koeltv.routing

import com.koeltv.model.MinecraftServer
import com.koeltv.plugins.ServerSendEvent
import com.koeltv.plugins.eventFlow
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

val minecraftServer = MinecraftServer()

val logFile = File("./minecraft.log").also {
    it.delete()
    it.createNewFile()
}

fun Route.configureMinecraftRoutes() {
    route("minecraft") {
        get {
            call.respond(FreeMarkerContent("control.ftl", mapOf("serverOn" to minecraftServer.isOn())))
        }

        post("start") {
            if (minecraftServer.isOff()) {
                application.log.info("Starting...")
                val isOn = minecraftServer.start { stream ->
                    launch(Dispatchers.IO) {
                        stream.bufferedReader().use {
                            it.lineSequence()
                                .forEach { line ->
                                    logFile.appendText("$line\n")
                                    eventFlow.emit(ServerSendEvent(line))
                                }
                        }
                    }
                }
                application.log.info(if (isOn) "Server ready !" else "Couldn't start server")
            }
            call.respondRedirect("/minecraft")
        }

        post("stop") {
            minecraftServer.stop()
            application.log.info("Server stopped")
            call.respondRedirect("/minecraft")
        }

        post("command") {
            if (minecraftServer.isOn()) {
                val command = call.receiveParameters().getOrFail("command")
                minecraftServer.inputCommand(command)
            }
            call.respondRedirect("/minecraft")
        }
    }
}
