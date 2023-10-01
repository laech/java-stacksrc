plugins {
  id("java")
}

repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  testImplementation(platform("org.junit:junit-bom:5.10.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("nz.lae.stacksrc:junit5:+")
}

tasks.test {
  useJUnitPlatform()
  systemProperty("junit.jupiter.extensions.autodetection.enabled", true)
  ignoreFailures = true
}
