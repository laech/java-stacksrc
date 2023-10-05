plugins {
  myproject.`java-conventions`
  myproject.`test-fixtures-conventions`
  myproject.`library-conventions`
  alias(libs.plugins.kotlin.jvm)
}

dependencies {
  testImplementation(libs.kotlin.stdlib)
  testFixturesImplementation(libs.jaxb.runtime)
}
