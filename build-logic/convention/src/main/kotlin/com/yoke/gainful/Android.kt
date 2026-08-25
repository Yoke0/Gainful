package com.yoke.gainful

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project

private const val GAINFUL_BASE_NAMESPACE = "com.yoke.gainful"

internal fun configureAndroid(
    project: Project,
    extension: KotlinMultiplatformAndroidLibraryTarget,
) {
    with(extension) {
        compileSdk = 37
        minSdk = 26
        namespace = project.gainfulNamespace()
        androidResources {
            enable = true
        }
    }
}

private fun Project.gainfulNamespace(): String {
    val qualifiedPath =
        path
            .trimStart(':')
            .split(':')
            .joinToString(".")
    return "$GAINFUL_BASE_NAMESPACE.$qualifiedPath"
}
