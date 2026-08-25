plugins {
    alias(libs.plugins.gainful.feature.library)
}

kotlin {
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "FeatureHoldings"
            isStatic = true
        }
    }
}
