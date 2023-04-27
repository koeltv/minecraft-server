package com.koeltv.routing

import com.koeltv.currentOs
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.InvalidPathException
import java.nio.file.Paths

private const val LIBRARIES_PATH = "server/libraries/net/minecraftforge/forge"

var serverProcess: Process? = null

fun Route.configureMinecraftRoutes() {
    route("minecraft") {
        get {
            call.respond(FreeMarkerContent("control.ftl", mapOf("serverOn" to (serverProcess != null))))
        }

        post("start") {
            if (serverProcess == null) {
                application.log.info("Starting...")
                serverProcess = startServer()
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
    }
}

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
                }_args.txt"
            )
            .inheritIO()
            .start()
    }
}
