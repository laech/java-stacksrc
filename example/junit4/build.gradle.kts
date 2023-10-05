plugins {
  myproject.`example-conventions`
}

dependencies {
  testExamplesImplementation(project(":junit4"))
  testExamplesImplementation(libs.junit4)
}

tasks.testExamples {
  useJUnit()
}
