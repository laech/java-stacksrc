plugins {
  myproject.`java-conventions`
  myproject.`test-integration-conventions`
  myproject.`library-conventions`
}

dependencies {
  api(project(":core"))
  api(libs.junit4)
  testImplementation(testFixtures(project(":core")))
}

tasks.withType<Test> {
  useJUnit()
  maxParallelForks = 2
}
