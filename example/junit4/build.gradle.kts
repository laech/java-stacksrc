plugins {
  myproject.`example-conventions`
}

dependencies {
  testExamplesImplementation(project(":junit4"))
  testExamplesImplementation("junit:junit:4.13.2")
}
