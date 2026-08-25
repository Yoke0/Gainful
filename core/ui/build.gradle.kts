plugins {
    alias(libs.plugins.gainful.kmp.compose.library)
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.designsystem)
            implementation(projects.core.common)
            implementation(projects.core.model)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.kotlinx.datetime)
            implementation(libs.compose.uiToolingPreview)
        }
    }
}
