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

  testIntegrationImplementation(libs.junit.jupiter)
}

tasks.withType<Test> {
  useJUnitPlatform()
  systemProperty("junit.jupiter.execution.parallel.enabled", true)
  systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
}
