plugins {
  myproject.`example-conventions`
}

dependencies {
  examplesImplementation(project(":testng"))
  examplesImplementation(libs.testng)
}

tasks.examples {
  useTestNG()
}
