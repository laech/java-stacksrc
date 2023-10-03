plugins {
  myproject.`java-conventions`
  `java-library`
}

dependencies {
  api(project(":core"))
  implementation("junit:junit:[4.13.1,)")

  testImplementation(testFixtures(project(":core")))
}

tasks.test {
  useJUnit()
}
