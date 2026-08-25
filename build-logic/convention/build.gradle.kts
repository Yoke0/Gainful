import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.yoke.gainful"

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.compose.gradle.plugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
    compileOnly(libs.kotlin.serialization.gradle.plugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("gainfulKmpLibrary") {
            id = "gainful.kmp.library"
            implementationClass = "GainfulKmpLibraryPlugin"
        }
        register("gainfulKmpAndroidLibrary") {
            id = "gainful.kmp.android.library"
            implementationClass = "GainfulKmpAndroidLibraryPlugin"
        }
        register("gainfulKmpComposeLibrary") {
            id = "gainful.kmp.compose.library"
            implementationClass = "GainfulKmpComposeLibraryPlugin"
        }
        register("gainfulFeatureLibrary") {
            id = "gainful.feature.library"
            implementationClass = "GainfulFeatureLibraryPlugin"
        }
    }
}
