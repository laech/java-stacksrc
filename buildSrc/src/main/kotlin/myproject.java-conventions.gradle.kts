import org.gradle.accessors.dm.LibrariesForLibs

plugins {
  java
  id("com.diffplug.spotless")
}

repositories {
  mavenCentral()
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

tasks.compileJava {
  options.release.set(11)
}

tasks.compileTestJava {
  options.release.set(17)
}

// https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()

spotless {
  java {
    googleJavaFormat()
  }
}

afterEvaluate {
  configurations
    .filter { it.name.endsWith("AnnotationProcessor", true) }
    .forEach {
      it.dependencies.add(libs.error.prune.core.get())
      it.dependencies.add(libs.nullaway.get())
    }
  configurations
    .filter { it.name.endsWith("compileOnly", true) }
    .forEach { it.dependencies.add(libs.jsr305.get()) }
}

tasks.withType<JavaCompile> {
  options.compilerArgs.addAll(
    listOf(
      "-XDcompilePolicy=simple",
      "-Xplugin:ErrorProne -Xep:NullAway:ERROR -XepOpt:NullAway:AnnotatedPackages=nz.lae.stacksrc"
    )
  )
}
