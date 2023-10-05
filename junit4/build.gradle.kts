plugins {
  myproject.`java-conventions`
  myproject.`test-integration-conventions`
  `java-library`
}

dependencies {
  api(project(":core"))
  implementation(libs.junit4)

  testImplementation(testFixtures(project(":core")))

  testIntegrationImplementation(testFixtures(project(":core")))
  testIntegrationImplementation(libs.jaxb.runtime)
}

tasks.test {
  useJUnit()
}

val copyTestIntegrationLibs by tasks.registering(Copy::class) {
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
