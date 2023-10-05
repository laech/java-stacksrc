plugins {
  idea
  id("myproject.java-conventions")
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
  outputs.upToDateWhen { false }
}

idea {
  module {
    testSources.from(testExamplesSourceSet.get().java.srcDirs)
  }
}
