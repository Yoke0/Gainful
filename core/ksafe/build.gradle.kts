plugins {
    alias(libs.plugins.gainful.kmp.android.library)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android {
        namespace = "com.yoke.gainful.ksafe"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ksafe)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
    }
}
