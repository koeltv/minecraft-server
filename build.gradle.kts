@file:Suppress("INACCESSIBLE_TYPE")

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    kotlin("jvm") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.beryx.runtime") version "1.13.0"
}

group = "com.koeltv"
version = "0.4.0"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-freemarker-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
}

fun relocateScript(sourceFile: File, destinationFile: File, replace: Pair<String, String>) {
    sourceFile.let { source ->
        destinationFile.createNewFile()
        source.useLines { lines ->
            lines.forEach {
                destinationFile.appendText(
                    "${it.replace(replace.first, replace.second)}${System.lineSeparator()}"
                )
            }
        }
        source.delete()
    }
}

tasks.create("restructureDist") {
    dependsOn(tasks.installShadowDist)

    doLast {
        val imageDir = "${project.buildDir}/install/${rootProject.name}-shadow"

        relocateScript(
            File("$imageDir/bin/${rootProject.name}"),
            File("$imageDir/${rootProject.name}"),
            "/.." to ""
        )

        relocateScript(
            File("$imageDir/bin/${rootProject.name}.bat"),
            File("$imageDir/${rootProject.name}.bat"),
            ".." to ""
        )
    }
}

tasks.jar {
    dependsOn("restructureDist")
}

runtime {
    options.set(listOf("--strip-debug", "--compress", "1", "--no-header-files", "--no-man-pages"))
    modules.set(listOf("java.naming", "jdk.unsupported", "jdk.unsupported.desktop"))
    imageZip.set(project.file("${project.buildDir}/dist/${rootProject.name}-${version}.zip"))

    val downloadPage = "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.7%2B7"

    targetPlatform("win") {
        setJdkHome(jdkDownload("${downloadPage}/OpenJDK17U-jdk_x64_windows_hotspot_17.0.7_7.zip"))
    }

    targetPlatform("linux") {
        setJdkHome(jdkDownload("${downloadPage}/OpenJDK17U-jdk_x64_linux_hotspot_17.0.7_7.tar.gz"))
    }

    targetPlatform("alpine-linux") {
        setJdkHome(jdkDownload("${downloadPage}/OpenJDK17U-jdk_x64_alpine-linux_hotspot_17.0.7_7.tar.gz"))
    }

    targetPlatform("mac") {
        setJdkHome(jdkDownload("${downloadPage}/OpenJDK17U-jdk_x64_mac_hotspot_17.0.7_7.tar.gz"))
    }
}