plugins {
    alias(libs.plugins.gainful.feature.library)
}

kotlin {
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "FeatureAccount"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.core.sync)
            implementation(projects.core.file)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
        }
    }
}
