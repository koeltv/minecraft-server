package com.koeltv.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.concurrent.thread

private const val LIBRARIES_PATH = "libraries/net/minecraftforge/forge"

private const val GUI = true

class MinecraftServer {
    private var process: Process? = null
    private var serverDirectory: Path = Paths.get("").resolve("server")

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
     * @param logProcessor a function taking in the console output of the minecraft server
     * @return true if the minecraft server was started successfully, false otherwise
     */
    fun start(logProcessor: (InputStream) -> Unit): Boolean {
        if (isOn()) return true

        if (!serverIsPresent()) {
            logProcessor("Server not found".byteInputStream())
            return false
        }

        if (!eulaAccepted()) {
            logProcessor("Please accept EULA before continuing".byteInputStream())
            return false
        }

        val forgeVersion = serverDirectory
            .resolve(LIBRARIES_PATH)
            .toFile()
            .list()
            ?.firstOrNull()
            ?: throw InvalidPathException("$serverDirectory/$LIBRARIES_PATH", "Incorrect server files structure")

        val arguments = mutableListOf<String>().apply {
            if (osPrefix == "win") {
                add("cmd.exe")
                add("/C")
            }

            add("java")
            add("@user_jvm_args.txt")
            add("@libraries/net/minecraftforge/forge/$forgeVersion/${osPrefix}_args.txt")

            if (!GUI) add("--nogui")
        }

        process = ProcessBuilder()
            .directory(serverDirectory.toFile())
            .command(arguments)
            .start()
            .also { logProcessor(it.inputStream) }

        return true
    }

    private fun serverIsPresent(): Boolean {
        return serverDirectory.toFile().listFiles()?.isNotEmpty() ?: false
    }

    private fun eulaAccepted(): Boolean {
        return serverDirectory.resolve("eula.txt")
            .toFile()
            .useLines {
                it.any { line -> line.lowercase().contains("eula=true") }
            }
    }

    fun stop() {
        process?.let {
            it.descendants().forEach { descendent -> descendent.destroy() }
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