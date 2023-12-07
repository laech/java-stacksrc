plugins {
  idea
  id("myproject.java-conventions")
}

val testIntegrationSourceSet: NamedDomainObjectProvider<SourceSet> =
  sourceSets.register("testIntegration") {
    val test = sourceSets.test.get()
    compileClasspath += test.compileClasspath
    runtimeClasspath += test.runtimeClasspath
  }

val testIntegrationImplementation: Configuration by configurations.getting {
  extendsFrom(configurations.testImplementation.get())
}

val testIntegrationRuntimeOnly: Configuration by configurations.getting {
  extendsFrom(configurations.testRuntimeOnly.get())
}

val testIntegrationCopyLibs by tasks.registering(Copy::class) {
  destinationDir =
    layout.buildDirectory.asFile.get().resolve("testIntegrationLibs")
  rename { it.replace("-${version}", "") }
  from(tasks.jar)
  from(project(":core").tasks.jar)
  dependsOn(project(":core").tasks.jar)
  dependsOn(tasks.jar)
}

val testIntegration = tasks.register<Test>("testIntegration") {
  description = "Runs integration tests."
  group = "verification"

  testClassesDirs = testIntegrationSourceSet.get().output.classesDirs
  classpath = testIntegrationSourceSet.get().runtimeClasspath

  // Register the test projects as inputs for incremental build, but ignoring their build
  // directories, i.e. only rerun integration test if their source files are changed.
  inputs.files(fileTree(projectDir.resolve("integration")) {
    exclude("*/.gradle")
    exclude("*/build")
    exclude("*/target")
  }).withPropertyName("testProjectFiles")

  shouldRunAfter(tasks.test)
  dependsOn(testIntegrationCopyLibs)
}

tasks.check {
  dependsOn(testIntegration)
}

idea {
  module {
    testSources.from(testIntegrationSourceSet.get().java.srcDirs)

    project.projectDir.resolve("integration").listFiles()?.forEach {
      excludeDirs.add(it.resolve(".gradle"))
      excludeDirs.add(it.resolve("build"))
      excludeDirs.add(it.resolve("target"))
    }
  }
}
