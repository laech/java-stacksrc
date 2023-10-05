plugins {
  myproject.`example-conventions`
}

dependencies {
  testExamplesImplementation(project(":junit5"))
  testExamplesImplementation(libs.junit.jupiter)
  testExamplesRuntimeOnly(libs.junit.platform.launcher)
}

tasks.testExamples {
  useJUnitPlatform()
}
