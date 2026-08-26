plugins {
    alias(libs.plugins.gainful.kmp.compose.library)
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

compose.resources {
    publicResClass = true
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.material3)
            implementation(libs.compose.uiToolingPreview)
        }
    }
}
