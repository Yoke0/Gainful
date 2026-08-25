import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.yoke.gainful.configureAndroid
import com.yoke.gainful.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByName
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class GainfulFeatureLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")
            pluginManager.apply("com.android.kotlin.multiplatform.library")
            pluginManager.apply("org.jetbrains.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            val catalog = libs

            extensions.configure<KotlinMultiplatformExtension> {
                iosArm64()
                iosSimulatorArm64()
                jvm()
                sourceSets.getByName("commonMain") {
                    dependencies {
                        // Compose
                        implementation(catalog.findLibrary("compose-runtime").get())
                        implementation(catalog.findLibrary("compose-foundation").get())
                        implementation(catalog.findLibrary("compose-material3").get())
                        implementation(catalog.findLibrary("compose-components-resources").get())
                        // Lifecycle + ViewModel
                        implementation(catalog.findLibrary("androidx-lifecycle-viewmodelCompose").get())
                        implementation(catalog.findLibrary("androidx-lifecycle-runtimeCompose").get())
                        implementation(catalog.findLibrary("koin-compose-viewmodel").get())
                        implementation(catalog.findLibrary("koin-core").get())
                        // Navigation
                        api(catalog.findLibrary("jetbrains-navigation3-ui").get())
                        implementation(project(":core:navigation"))
                        // Core shared
                        implementation(project(":core:common"))
                        implementation(project(":core:model"))
                        implementation(project(":core:domain"))
                        implementation(project(":core:designsystem"))
                        implementation(project(":core:ui"))
                        // Serialization
                        implementation(catalog.findLibrary("kotlinx-serialization-json").get())
                        implementation(catalog.findLibrary("kotlinx-datetime").get())
                    }
                }
            }

            val kotlinExtension = extensions.getByType(KotlinMultiplatformExtension::class.java)
            val androidExtension =
                (kotlinExtension as ExtensionAware)
                    .extensions
                    .getByType(KotlinMultiplatformAndroidLibraryTarget::class.java)
            configureAndroid(this, androidExtension)
        }
    }
}
