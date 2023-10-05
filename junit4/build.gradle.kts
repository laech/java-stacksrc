plugins {
  myproject.`java-conventions`
  myproject.`test-integration-conventions`
  `java-library`
}

dependencies {
  api(project(":core"))
  implementation(libs.junit4)
  testImplementation(testFixtures(project(":core")))
}

tasks.test {
  useJUnit()
}
