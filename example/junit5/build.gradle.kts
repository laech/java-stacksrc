plugins {
  myproject.`example-conventions`
}

dependencies {
  testImplementation(project(":junit5"))
  testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
  useJUnitPlatform()
}
