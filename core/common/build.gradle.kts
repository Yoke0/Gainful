import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

val versionName = property("VERSION_NAME").toString()

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
            |}
            """.trimMargin(),
        )
    }
}

val generateBuildConfig =
    tasks.register<GenerateBuildConfigTask>("generateBuildConfig") {
        description = "Generates BuildConfig.kt with app version"
        versionName.set(project.property("VERSION_NAME").toString())
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
