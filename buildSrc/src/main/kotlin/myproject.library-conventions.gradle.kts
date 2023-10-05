// https://central.sonatype.org/publish/publish-guide/
// https://central.sonatype.org/publish/publish-gradle/
// https://www.jetbrains.com/help/space/publish-artifacts-to-maven-central.html
// https://docs.gradle.org/current/userguide/publishing_maven.html
// https://docs.gradle.org/current/samples/sample_publishing_credentials.html

plugins {
  `java-library`
  `maven-publish`
}

group = "nz.lae.stacksrc"
version = "0.5.0-SNAPSHOT"

tasks.jar {
  archiveBaseName.set("stacksrc-${project.name}")
}

java {
  withSourcesJar()
  withJavadocJar()
}

publishing {
  publications {
    create<MavenPublication>("maven") {

      // Don't publish test fixtures
      val comp = components["java"] as AdhocComponentWithVariants
      if (plugins.hasPlugin("java-test-fixtures")) {
        listOf(
          "testFixturesApiElements", "testFixturesRuntimeElements"
        ).forEach {
          comp.withVariantsFromConfiguration(configurations[it]) { skip() }
        }
      }
      from(comp)

      versionMapping {
        usage("java-api") {
          fromResolutionOf("runtimeClasspath")
        }
        usage("java-runtime") {
          fromResolutionResult()
        }
      }

      pom {
        artifactId = tasks.jar.get().archiveBaseName.get()
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
      }
    }
  }

  repositories {
    maven {
      name = "ossrh"
      url = if (version.toString().endsWith("SNAPSHOT")) {
        uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
      } else {
        uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
      }
      credentials(PasswordCredentials::class)
    }
  }
}

tasks.javadoc {
  val opts = options as StandardJavadocDocletOptions
  opts.addBooleanOption("html5", true)
  opts.addStringOption("Xdoclint:none", "-quiet")
}