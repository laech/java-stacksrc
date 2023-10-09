plugins {
  myproject.`java-conventions`
  myproject.`test-integration-conventions`
  myproject.`library-conventions`
}

dependencies {
  api(project(":core"))
  implementation(libs.testng)

  testImplementation(testFixtures(project(":core")))
}

tasks.test {
  useTestNG()
}
