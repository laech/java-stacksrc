plugins {
  java
}

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(project(":junit4"))
  testImplementation("junit:junit:4.13.2")
}

tasks.test {
  ignoreFailures = true
}
