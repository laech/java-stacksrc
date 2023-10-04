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
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

spotless {
  java {
    googleJavaFormat()
  }
}

tasks.test {
  onlyIf {
    project.hasProperty("includeExamples")
  }
  ignoreFailures = true
}
