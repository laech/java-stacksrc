plugins {
  myproject.`example-conventions`
}

dependencies {
  testExamplesImplementation(project(":logback"))
  testExamplesImplementation(libs.logback.classic)
  testExamplesImplementation(libs.junit.jupiter)
  testExamplesRuntimeOnly(libs.junit.platform.launcher)
}

tasks.testExamples {
  useJUnitPlatform()
}
