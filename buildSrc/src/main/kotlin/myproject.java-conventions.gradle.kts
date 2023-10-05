import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  java
  `java-test-fixtures`
  idea
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

val testIntegrationSourceSet: NamedDomainObjectProvider<SourceSet> =
  sourceSets.register("testIntegration") {
    val main = sourceSets.main.get()
    compileClasspath += main.compileClasspath
    runtimeClasspath += main.runtimeClasspath
  }

val testIntegrationImplementation: Configuration by configurations.getting {
  extendsFrom(configurations.testImplementation.get())
}

val testIntegrationRuntimeOnly: Configuration by configurations.getting {
  extendsFrom(configurations.testRuntimeOnly.get())
}

val testIntegration = tasks.register<Test>("testIntegration") {
  description = "Runs integration tests."
  group = "verification"

  testClassesDirs = testIntegrationSourceSet.get().output.classesDirs
  classpath = testIntegrationSourceSet.get().runtimeClasspath

  shouldRunAfter(tasks.test)
}

tasks.check {
  dependsOn(testIntegration)
}

tasks.withType<Test> {
  useJUnitPlatform()
  systemProperty("junit.jupiter.execution.parallel.enabled", true)
  systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
  testLogging {
    showExceptions = true
    exceptionFormat = TestExceptionFormat.FULL
  }
}

// https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()

dependencies {

  testFixturesImplementation(libs.junit.jupiter)

  testImplementation(libs.junit.jupiter)
  testRuntimeOnly(libs.junit.platform.launcher)

  testIntegrationImplementation(libs.junit.jupiter)
  testIntegrationRuntimeOnly(libs.junit.platform.launcher)
}

idea {
  module {
    testSources.from(testIntegrationSourceSet.get().java.srcDirs)
  }
}

spotless {
  java {
    googleJavaFormat()
  }
}
