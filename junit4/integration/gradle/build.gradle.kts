plugins {
  java
}

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("junit:junit:4.13.2")
  testImplementation(fileTree("../../build/testIntegrationLibs"))
}

tasks.test {
  ignoreFailures = true
}
