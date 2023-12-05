plugins {
  myproject.`example-conventions`
}

dependencies {
  examplesImplementation(project(":logback"))
  examplesImplementation(libs.logback.classic)
  examplesImplementation(libs.junit.jupiter)
  examplesRuntimeOnly(libs.junit.platform.launcher)
}

tasks.examples {
  useJUnitPlatform()
}
