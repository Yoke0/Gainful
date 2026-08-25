import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.yoke.gainful.configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class GainfulKmpAndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")
            pluginManager.apply("com.android.kotlin.multiplatform.library")

            extensions.configure<KotlinMultiplatformExtension> {
                iosArm64()
                iosSimulatorArm64()
                jvm()
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
