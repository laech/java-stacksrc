plugins {
  java
  idea
  id("com.diffplug.spotless")
}

repositories {
  mavenCentral()
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

val testExamplesSourceSet: NamedDomainObjectProvider<SourceSet> =
  sourceSets.register("testExamples")

val testExamplesImplementation: Configuration by configurations.getting {
  extendsFrom(configurations.testImplementation.get())
}

val testExamplesRuntimeOnly: Configuration by configurations.getting {
  extendsFrom(configurations.testRuntimeOnly.get())
}

val testExamples = tasks.register<Test>("testExamples") {
  description = "Runs example tests."
  group = "example"
  testClassesDirs = testExamplesSourceSet.get().output.classesDirs
  classpath = testExamplesSourceSet.get().runtimeClasspath
  ignoreFailures = true
}

idea {
  module {
    testSources.from(testExamplesSourceSet.get().java.srcDirs)
  }
}

spotless {
  java {
    googleJavaFormat()
  }
}
