plugins {
  myproject.`example-conventions`
}

dependencies {
  testExamplesImplementation(project(":testng"))
  testExamplesImplementation(libs.testng)
}

tasks.testExamples {
  useTestNG()
}
