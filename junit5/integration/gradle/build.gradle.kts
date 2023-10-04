plugins {
  java
}

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(platform("org.junit:junit-bom:5.10.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation(fileTree("../../build/testIntegrationLibs"))
  testRuntimeOnly("org.junit.platform:junit-platform-reporting")
}

tasks.test {
  useJUnitPlatform()
  systemProperty("junit.jupiter.extensions.autodetection.enabled", true)
  ignoreFailures = true
  jvmArgumentProviders += CommandLineArgumentProvider {
    listOf(
      "-Djunit.platform.reporting.open.xml.enabled=true",
      "-Djunit.platform.reporting.output.dir=${reports.junitXml.outputLocation.get().asFile.absolutePath}"
    )
  }
}
