@file:Suppress("UnstableApiUsage")

rootProject.name = "Gainful"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":androidApp")
include(":desktopApp")
include(":shared")

include(":core:common")
include(":core:model")
include(":core:designsystem")
include(":core:data")
include(":core:database")
include(":core:network")
include(":core:domain")
include(":core:ui")
include(":core:navigation")
include(":core:datastore")
include(":core:ksafe")
include(":core:sync")
include(":core:file")
include(":core:widget")

include(":feature:dashboard")
include(":feature:holdings")
include(":feature:transactions")
include(":feature:settings")
include(":feature:account")

include(":api:contract")
include(":server")
