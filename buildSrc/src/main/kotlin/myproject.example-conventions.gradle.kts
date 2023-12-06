plugins {
  idea
  id("myproject.java-conventions")
}

val examplesSourceSet: NamedDomainObjectProvider<SourceSet> =
  sourceSets.register("examples")

val examplesImplementation: Configuration by configurations.getting {
  extendsFrom(configurations.testImplementation.get())
}

val examplesRuntimeOnly: Configuration by configurations.getting {
  extendsFrom(configurations.testRuntimeOnly.get())
}

val examples = tasks.register<Test>("examples") {
  description = "Runs examples."
  group = "example"
  testClassesDirs = examplesSourceSet.get().output.classesDirs
  classpath = examplesSourceSet.get().runtimeClasspath
  ignoreFailures = true
  outputs.upToDateWhen { false }
}

idea {
  module {
    testSources.from(examplesSourceSet.get().java.srcDirs)
  }
}
