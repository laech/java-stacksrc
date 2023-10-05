plugins {
  `java-library`
  `maven-publish`
}

java {
  withSourcesJar()
  withJavadocJar()
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])
      versionMapping {
        usage("java-api") {
          fromResolutionOf("runtimeClasspath")
        }
        usage("java-runtime") {
          fromResolutionResult()
        }
      }
      pom {
        groupId = "nz.lae.stacksrc"
        artifactId = "stacksrc-${project.name}"
        version = "0.5.0-SNAPSHOT"
        url.set("https://github.com/laech/java-stacksrc")
        scm {
          url.set("https://github.com/laech/java-stacksrc")
          developerConnection.set("scm:git:https://github.com/laech/java-stacksrc.git")
        }
        licenses {
          license {
            name.set("The Apache License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
          }
        }
        distributionManagement {
          repositories {

          }
        }
      }
    }
  }
}

tasks.javadoc {
  (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
}
