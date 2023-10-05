import org.gradle.accessors.dm.LibrariesForLibs

plugins {
  idea
  id("myproject.java-conventions")
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

// https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()

dependencies {
  testIntegrationImplementation(libs.junit.jupiter)
  testIntegrationRuntimeOnly(libs.junit.platform.launcher)
}

idea {
  module {
    testSources.from(testIntegrationSourceSet.get().java.srcDirs)
  }
}
