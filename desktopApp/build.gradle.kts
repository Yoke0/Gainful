import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutines.swing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "com.yoke.gainful.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Gainful"
            packageVersion = property("VERSION_NAME").toString()
            description = "Gainful Desktop Application"
            macOS {
                bundleID = "com.yoke.gainful"
                iconFile.set(project.file("src/main/resources/icon.icns"))
            }
        }
    }
}

tasks.register<Exec>("createDmg") {
    description = "Create DMG with Applications symlink for macOS distribution"
    group = "distribution"
    dependsOn("packageDmg")

    val buildDir = layout.buildDirectory.get().asFile
    val appPath = File(buildDir, "compose/binaries/main/app/Gainful.app").absolutePath
    val dmgPath = File(buildDir, "compose/binaries/main/dmg/Gainful.dmg").absolutePath
    val scriptPath = rootProject.file("scripts/create-dmg.sh").absolutePath

    commandLine("bash", scriptPath, appPath, dmgPath)
}
