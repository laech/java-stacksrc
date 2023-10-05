plugins {
  myproject.`java-conventions`
  myproject.`test-integration-conventions`
  `java-library`
}

dependencies {
  api(project(":core"))
  implementation("org.junit.jupiter:junit-jupiter-api:[5.0.0,)")

  compileOnly(libs.auto.service.annotations)
  annotationProcessor(libs.auto.service.processor)

  testImplementation(testFixtures(project(":core")))
}
