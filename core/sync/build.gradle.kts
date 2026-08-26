plugins {
    alias(libs.plugins.gainful.kmp.android.library)
}

kotlin {
    android {
        namespace = "com.yoke.gainful.sync"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.common)
            implementation(projects.core.data)
            implementation(projects.core.network)
            implementation(projects.core.datastore)
            implementation(projects.core.domain)
            implementation(projects.core.widget)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
        }
    }
}
