plugins {
    alias(libs.plugins.gainful.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.api.contract)
            implementation(projects.core.model)
            implementation(projects.core.common)
            implementation(projects.core.network)
            implementation(projects.core.database)
            implementation(projects.core.datastore)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            implementation(libs.ktor.client.core)
        }
    }
}
