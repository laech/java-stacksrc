plugins {
  application
}

repositories {
  mavenCentral()
}

val developmentOnly: Configuration by configurations.creating {
  extendsFrom(configurations.runtimeOnly.get())
}

dependencies {
  implementation("ch.qos.logback:logback-classic:1.4.9")
  testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
  developmentOnly(fileTree("../../build/testIntegrationLibs"))
}

application {
  mainClass = "nz.lae.stacksrc.test.integration.GradleLogbackExample"
}

tasks.withType<JavaExec> {
  classpath += developmentOnly
}

tasks.withType<Test> {
  useJUnitPlatform()
  classpath += developmentOnly
}
