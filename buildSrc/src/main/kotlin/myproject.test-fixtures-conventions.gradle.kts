import org.gradle.accessors.dm.LibrariesForLibs

plugins {
  `java-test-fixtures`
  id("myproject.java-conventions")
}

// https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()

dependencies {
  testFixturesImplementation(libs.junit.jupiter)
}
