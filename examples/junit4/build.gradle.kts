plugins {
  myproject.`example-conventions`
}

dependencies {
  examplesImplementation(project(":junit4"))
  examplesImplementation(libs.junit4)
}

tasks.examples {
  useJUnit()
}
