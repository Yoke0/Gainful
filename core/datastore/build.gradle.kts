plugins {
    alias(libs.plugins.gainful.kmp.android.library)
}

kotlin {
    android {
        namespace = "com.yoke.gainful.datastore"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(projects.core.proto)
            implementation(libs.datastore.preferences)
            implementation(libs.datastore.okio)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
    }
}
