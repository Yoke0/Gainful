plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.wire)
}

wire {
    kotlin {}
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    )

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.wire.runtime)
        }
    }
}
