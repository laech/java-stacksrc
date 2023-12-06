plugins {
  myproject.`java-conventions`
  myproject.`test-integration-conventions`
  myproject.`library-conventions`
}

dependencies {
  implementation(project(":core"))
  implementation(libs.logback.classic)

  testImplementation(testFixtures(project(":core")))
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.assertj.core)

  testIntegrationImplementation(libs.junit.jupiter)
  testImplementation(libs.assertj.core)
}

tasks.withType<Test> {
  useJUnitPlatform()
  systemProperty("junit.jupiter.execution.parallel.enabled", true)
  systemProperty("junit.jupiter.execution.parallel.mode.default", "same_thread")
  systemProperty("junit.jupiter.execution.parallel.mode.classes.default", "concurrent")
}
