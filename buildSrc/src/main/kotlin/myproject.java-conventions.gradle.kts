import org.gradle.accessors.dm.LibrariesForLibs

plugins {
  java
  id("com.diffplug.spotless")
}

repositories {
  mavenCentral()
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

tasks.compileJava {
  options.release.set(11)
}

tasks.compileTestJava {
  options.release.set(17)
}

// https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()

spotless {
  java {
    googleJavaFormat()
  }
}
