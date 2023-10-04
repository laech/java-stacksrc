plugins {
  myproject.`java-conventions`
  `java-library`
}

dependencies {
  api(project(":core"))
  implementation("org.junit.jupiter:junit-jupiter-api:[5.0.0,)")

  compileOnly(libs.auto.service.annotations)
  annotationProcessor(libs.auto.service.processor)

  testImplementation(testFixtures(project(":core")))

  testIntegrationImplementation(testFixtures(project(":core")))
  testIntegrationImplementation("org.glassfish.jaxb:jaxb-runtime:4.0.3")
}

val copyTestIntegrationLibs by
    tasks.registering(Copy::class) {
      destinationDir = buildDir.resolve("testIntegrationLibs")
      from(tasks.jar)
      from(project(":core").tasks.jar)
      mustRunAfter(tasks.jar)
    }

tasks.testIntegration {
  dependsOn(copyTestIntegrationLibs)

  // Register the test projects as inputs for incremental build, but ignoring their build
  // directories, i.e. only rerun integration test if their source files are changed.
  inputs
      .files(fileTree(projectDir.resolve("integration/gradle")) { exclude(".gradle", "build") })
      .withPropertyName("gradleTestProjectDir")
  inputs
      .files(fileTree(projectDir.resolve("integration/maven")) { exclude("target") })
      .withPropertyName("mavenTestProjectDir")
}