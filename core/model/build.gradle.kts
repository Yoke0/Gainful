plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    )

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
