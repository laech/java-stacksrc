plugins {
  myproject.`java-conventions`
  myproject.`test-integration-conventions`
  myproject.`library-conventions`
}

dependencies {
  api(project(":core"))
  implementation(libs.testng)

  testImplementation(testFixtures(project(":core")))
  testIntegrationImplementation(libs.testng)
}

tasks.withType<Test> {
  useTestNG()
  (options as TestNGOptions).parallel = "classes"
}
