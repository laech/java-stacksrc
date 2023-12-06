plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

include("core")
include("junit4")
include("junit5")
include("testng")
include("logback")
include("examples:junit4")
include("examples:junit5")
include("examples:testng")
include("examples:logback")
