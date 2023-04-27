package com.koeltv.routing

import com.koeltv.currentOs
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
import kotlinx.coroutines.withContext
import java.nio.file.InvalidPathException
import java.nio.file.Paths

private const val LIBRARIES_PATH = "server/libraries/net/minecraftforge/forge"

var serverProcess: Process? = null

const val GUI = true

fun Route.configureMinecraftRoutes() {
    route("minecraft") {
        get {
            call.respond(FreeMarkerContent("control.ftl", mapOf("serverOn" to (serverProcess != null))))
        }

        post("start") {
            if (serverProcess == null) {
                application.log.info("Starting...")
                serverProcess = startServer().also { process ->
                    launch(Dispatchers.IO) {
                        process.inputStream.bufferedReader().use {
                            it
                                .lineSequence()
                                .forEach { line -> eventFlow.emit(ServerSendEvent(line)) }
                        }
                    }
                }
                application.log.info("Server ready !")
            }
            call.respondRedirect("/minecraft")
        }

        post("stop") {
            serverProcess?.let {
                it.destroy()
                serverProcess = null
                application.log.info("Server stopped")
            }
            call.respondRedirect("/minecraft")
        }

        post("command") {
            serverProcess?.let { process ->
                val command = call.receiveParameters().getOrFail("command")
                launch(Dispatchers.IO) {
                    process.outputStream.bufferedWriter().let {
                        it.write("$command\n")
                        it.flush()
                    }
                }
            }
            call.respondRedirect("/minecraft")
        }
    }
}

/**
 * Start the minecraft server
 * @return the running server [Process]
 */
private suspend fun startServer(): Process {
    return withContext(Dispatchers.IO) {
        val currentDirectory = Paths.get("")

        val forgeVersion = currentDirectory
            .resolve(LIBRARIES_PATH)
            .toFile()
            .list()
            ?.firstOrNull()
            ?: throw InvalidPathException("$this/$LIBRARIES_PATH", "Incorrect server files structure")

        val serverDirectory = currentDirectory.resolve("server").toFile()

        ProcessBuilder()
            .directory(serverDirectory)
            .command(
                "java",
                "@user_jvm_args.txt",
                "@libraries/net/minecraftforge/forge/$forgeVersion/${
                    if (currentOs.contains("win")) {
                        "win"
                    } else {
                        "unix"
                    }
                }_args.txt ${if (GUI) "" else "--nogui"}"
            )
            .start()
    }
}
