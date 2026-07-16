import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

val versionNameValue = property("VERSION_NAME").toString()

val localProps =
    Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            file.inputStream().use { load(it) }
        }
    }
val serverBaseUrlValue = localProps.getProperty("SERVER_BASE_URL", "")

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    )

    jvm()

    android {
        namespace = "com.yoke.gainful.core.common"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
        jvmMain.dependencies {
            implementation(libs.icu4j)
        }
    }
}

abstract class GenerateBuildConfigTask : DefaultTask() {
    @get:Input
    abstract val versionName: Property<String>

    @get:Input
    abstract val serverBaseUrl: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val dir = outputDir.get().asFile.resolve("com/yoke/gainful/common")
        dir.mkdirs()
        dir.resolve("BuildConfig.kt").writeText(
            """
            |package com.yoke.gainful.common
            |
            |object BuildConfig {
            |    const val APP_VERSION = "${versionName.get()}"
            |    const val SERVER_BASE_URL = "${serverBaseUrl.get()}"
            |}
            """.trimMargin(),
        )
    }
}

val generateBuildConfig =
    tasks.register<GenerateBuildConfigTask>("generateBuildConfig") {
        description = "Generates BuildConfig.kt with app version"
        versionName.set(versionNameValue)
        serverBaseUrl.set(serverBaseUrlValue)
        outputDir.set(layout.buildDirectory.dir("generated/buildconfig"))
    }

tasks.matching { it.name.startsWith("compileKotlin") }.configureEach {
    dependsOn(generateBuildConfig)
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir(generateBuildConfig.map { layout.buildDirectory.dir("generated/buildconfig") })
        }
    }
}
