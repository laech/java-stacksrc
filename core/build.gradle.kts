plugins {
  myproject.`java-conventions`
  myproject.`library-conventions`
  `java-test-fixtures`
  alias(libs.plugins.kotlin.jvm)
}

dependencies {
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.kotlin.stdlib)
  testFixturesImplementation(libs.junit.jupiter.api)
  testFixturesImplementation(libs.jaxb.runtime)
}

tasks.withType<Test> {
  useJUnitPlatform()
}
