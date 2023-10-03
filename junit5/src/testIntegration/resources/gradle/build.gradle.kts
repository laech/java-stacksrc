plugins {
  id("java")
}

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(platform("org.junit:junit-bom:5.10.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation(fileTree("../libs"))
}

tasks.test {
  useJUnitPlatform()
  systemProperty("junit.jupiter.extensions.autodetection.enabled", true)
  ignoreFailures = true
}
