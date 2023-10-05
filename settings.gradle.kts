plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

include("core")
include("junit4")
include("junit5")
include("example:junit4")
include("example:junit5")
