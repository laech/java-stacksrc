plugins {
  myproject.`example-conventions`
}

dependencies {
  testExamplesImplementation(project(":junit5"))
  testExamplesImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
  testExamplesRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.testExamples {
  useJUnitPlatform()
}
