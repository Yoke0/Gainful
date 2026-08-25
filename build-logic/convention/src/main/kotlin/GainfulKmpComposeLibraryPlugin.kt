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

class GainfulKmpComposeLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")
            pluginManager.apply("com.android.kotlin.multiplatform.library")
            pluginManager.apply("org.jetbrains.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            val catalog = libs

            extensions.configure<KotlinMultiplatformExtension> {
                iosArm64()
                iosSimulatorArm64()
                jvm()
                sourceSets.getByName("commonMain") {
                    dependencies {
                        implementation(catalog.findLibrary("compose-runtime").get())
                        implementation(catalog.findLibrary("compose-foundation").get())
                        implementation(catalog.findLibrary("compose-ui").get())
                        implementation(catalog.findLibrary("compose-components-resources").get())
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
