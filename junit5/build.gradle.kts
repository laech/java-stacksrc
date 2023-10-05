plugins {
  myproject.`java-conventions`
  myproject.`test-integration-conventions`
  myproject.`library-conventions`
}

dependencies {
  api(project(":core"))
  api(libs.junit.jupiter.api)

  testImplementation(testFixtures(project(":core")))
}
