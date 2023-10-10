plugins {
  java
}

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("org.testng:testng:7.8.0")
  testImplementation(fileTree("../../build/testIntegrationLibs"))
}

tasks.test {
  useTestNG()
  ignoreFailures = true
}
