plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

rootProject.name = "stacksrc"
include("core")
include("junit4")
include("junit5")
include("example:junit4")
include("example:junit5")

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      val autoValue = version("auto-value", "1.10.4")
      library("auto-value-annotations", "com.google.auto.value", "auto-value-annotations").versionRef(autoValue)
      library("auto-value-processor", "com.google.auto.value", "auto-value").versionRef(autoValue)

      val autoService = version("auto-service", "1.1.1")
      library("auto-service-annotations", "com.google.auto.service", "auto-service-annotations").versionRef(autoService)
      library("auto-service-processor", "com.google.auto.service", "auto-service").versionRef(autoService)

      val kotlin = version("kotlin", "1.9.10")
      library("kotlin-stdlib", "org.jetbrains.kotlin", "kotlin-stdlib").versionRef(kotlin)
      plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").versionRef(kotlin)
    }
  }
}
