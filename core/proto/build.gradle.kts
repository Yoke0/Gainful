plugins {
    alias(libs.plugins.gainful.kmp.library)
    alias(libs.plugins.wire)
}

wire {
    kotlin {}
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.wire.runtime)
        }
    }
}
