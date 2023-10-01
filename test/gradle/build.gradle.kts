import groovy.util.Node
import groovy.util.NodeList
import groovy.xml.XmlParser

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

  val pom = XmlParser().parse(File("../pom.xml"))
  val parent = (pom.get("parent") as NodeList)[0] as Node
  val version = (parent.get("version") as NodeList)[0] as Node
  testImplementation("nz.lae.stacksrc:junit5:${version.value()}")
}

tasks.test {
  useJUnitPlatform()
  systemProperty("junit.jupiter.extensions.autodetection.enabled", true)
  ignoreFailures = true
}
