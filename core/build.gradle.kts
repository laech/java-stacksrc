plugins {
  myproject.`java-conventions`
  alias(libs.plugins.kotlin.jvm)
}

dependencies {
  compileOnly(libs.auto.value.annotations)
  annotationProcessor(libs.auto.value.processor)

  testImplementation(libs.kotlin.stdlib)
}
