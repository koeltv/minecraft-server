package com.koeltv.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.InvalidPathException
import java.nio.file.Paths
import kotlin.concurrent.thread

private const val LIBRARIES_PATH = "server/libraries/net/minecraftforge/forge"

private const val GUI = true

class MinecraftServer {
    private var process: Process? = null

    companion object {
        val osPrefix by lazy {
            System.getProperty("os.name").lowercase().let {
                if (it.contains("win")) {
                    "win"
                } else {
                    "unix"
                }
            }
        }
    }

    init {
        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            runBlocking {
                stop()
            }
        })
    }

    fun isOn(): Boolean {
        return process != null
    }

    fun isOff(): Boolean {
        return !isOn()
    }

    /**
     * Start the minecraft server
     * @return the running server [Process]
     */
    fun start(logProcessor: (InputStream) -> Unit) {
        if (isOn()) return

        val currentDirectory = Paths.get("")

        val forgeVersion = currentDirectory
            .resolve(LIBRARIES_PATH)
            .toFile()
            .list()
            ?.firstOrNull()
            ?: throw InvalidPathException("$this/$LIBRARIES_PATH", "Incorrect server files structure")

        val serverDirectory = currentDirectory.resolve("server").toFile()

        process = ProcessBuilder()
            .directory(serverDirectory)
            .command(
                "java",
                "@user_jvm_args.txt",
                "@libraries/net/minecraftforge/forge/$forgeVersion/${osPrefix}_args.txt",
                if (GUI) "" else "--nogui"
            )
            .start()
            .also { logProcessor(it.inputStream) }
    }

    fun stop() {
        if (isOff()) return

        process?.let {
            it.destroy()
            process = null
        }
    }

    suspend fun inputCommand(command: String) {
        withContext(Dispatchers.IO) {
            launch {
                process?.outputStream?.bufferedWriter()?.let {
                    it.write("$command\n")
                    it.flush()
                }
            }
        }
    }
}