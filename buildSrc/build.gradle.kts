plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
}

dependencies {
  // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(libs.spotless)
}
