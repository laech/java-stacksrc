plugins {
  myproject.`java-conventions`
  myproject.`library-conventions`
  `java-test-fixtures`
}

dependencies {
  testImplementation(libs.junit.jupiter)
  testFixturesImplementation(libs.junit.jupiter.api)
  testFixturesImplementation(libs.jaxb.runtime)
}

tasks.withType<Test> {
  useJUnitPlatform()
}
