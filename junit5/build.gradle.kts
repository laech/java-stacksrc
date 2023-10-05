plugins {
  myproject.`java-conventions`
  myproject.`test-integration-conventions`
  myproject.`library-conventions`
}

dependencies {
  api(project(":core"))
  api(libs.junit.jupiter.api)

  compileOnly(libs.auto.service.annotations)
  annotationProcessor(libs.auto.service.processor)

  testImplementation(testFixtures(project(":core")))
}
