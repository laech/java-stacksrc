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

tasks.test {
  useJUnitPlatform()
  testLogging {
    showExceptions = true
    exceptionFormat = TestExceptionFormat.FULL
  }
}

val testIntegrationSourceSet: NamedDomainObjectProvider<SourceSet> =
  sourceSets.register("testIntegration") {
    val main = sourceSets.main.get()
    compileClasspath += main.compileClasspath
    runtimeClasspath += main.runtimeClasspath
  }

val testIntegrationImplementation: Configuration by configurations.getting {
  extendsFrom(configurations.implementation.get())
}

val testIntegrationRuntimeOnly: Configuration by configurations.getting {
  extendsFrom(configurations.runtimeOnly.get())
}

val testIntegration = tasks.register<Test>("testIntegration") {
  description = "Runs integration tests."
  group = "verification"

  testClassesDirs = testIntegrationSourceSet.get().output.classesDirs
  classpath = testIntegrationSourceSet.get().runtimeClasspath

  shouldRunAfter(tasks.test)
  useJUnitPlatform()
}

tasks.check {
  dependsOn(testIntegration)
}

dependencies {
  val junitVersion = "5.10.0"

  testFixturesImplementation(platform("org.junit:junit-bom:${junitVersion}"))
  testFixturesImplementation("org.junit.jupiter:junit-jupiter")

  testImplementation(platform("org.junit:junit-bom:${junitVersion}"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")

  testIntegrationImplementation(platform("org.junit:junit-bom:${junitVersion}"))
  testIntegrationImplementation("org.junit.jupiter:junit-jupiter")
  testIntegrationRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
