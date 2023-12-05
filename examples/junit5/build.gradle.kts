plugins {
  myproject.`example-conventions`
}

dependencies {
  examplesImplementation(project(":junit5"))
  examplesImplementation(libs.junit.jupiter)
  examplesRuntimeOnly(libs.junit.platform.launcher)
}

tasks.examples {
  useJUnitPlatform()
}
