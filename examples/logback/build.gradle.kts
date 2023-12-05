plugins {
  myproject.`example-conventions`
}

val developmentOnly: Configuration by configurations.creating {
  extendsFrom(configurations.runtimeOnly.get())
}

dependencies {
  developmentOnly(project(":logback"))
  examplesImplementation(libs.logback.classic)
  examplesImplementation(libs.junit.jupiter)
  examplesRuntimeOnly(libs.junit.platform.launcher)
}

tasks.examples {
  useJUnitPlatform()
  classpath += developmentOnly
}
