plugins {
  java
}

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(project(":junit5"))
  testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
  useJUnitPlatform()
  ignoreFailures = true
}
