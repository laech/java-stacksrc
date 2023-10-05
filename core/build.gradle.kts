plugins {
  myproject.`java-conventions`
  myproject.`test-fixtures-conventions`
  myproject.`library-conventions`
  alias(libs.plugins.kotlin.jvm)
}

dependencies {
  compileOnly(libs.auto.value.annotations)
  annotationProcessor(libs.auto.value.processor)

  testImplementation(libs.kotlin.stdlib)

  testFixturesImplementation(libs.jaxb.runtime)
}
