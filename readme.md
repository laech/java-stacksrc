# Overview

![Maven Central](https://img.shields.io/maven-central/v/nz.lae.stacksrc/stacksrc-core?color=blue)

The goal of this project is to decorate stack traces of test failures to make
them more useful.

So instead of getting this when a test fails:

```
org.opentest4j.AssertionFailedError: expected: <hello> but was: <hi>
	at org.junit...
	at com.example.MyTest.hello(MyTest.java:24)
	...
```

You'll get this:

```
org.opentest4j.AssertionFailedError: expected: <hello> but was: <hi>
	at org.junit...
	at com.example.MyTest.hello(MyTest.java:24)

	   22    @Test
	   23    void compareStrings() {
	-> 24      assertEquals("hello", "hi");
	   25    }

	...
```

In both your IDE and build server test reports. Also works for other JVM
languages.

## Usage

Requires Java 11.

### Gradle

```groovy
dependencies {
  // For JUnit 5
  testImplementation("nz.lae.stacksrc:stacksrc-junit5:${stacksrc.version}")
  // For JUnit 4
  testImplementation("nz.lae.stacksrc:stacksrc-junit5:${stacksrc.version}")
}
```

### Maven

```xml

<dependency>
  <groupId>nz.lae.stacksrc</groupId>
  <artifactId>stacksrc-junit5</artifactId> <!-- For JUnit 5 -->
  <artifactId>stacksrc-junit4</artifactId> <!-- For JUnit 4 -->
  <version>${stacksrc.version}</version>
  <scope>test</scope>
</dependency>
```

### JUnit 5

For JUnit 5, you can
[enable automatic extension detection](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-automatic-enabling)
by setting the system
property `junit.jupiter.extensions.autodetection.enabled=true` on your build,
then no other change will be needed for this to work.

If you don't want to enable automatic extension detection, you can wire things
up manually using either JUnit's `@ExtendWith` or `@RegisterExtension` like the
following, then all tests inheriting `BaseTest` will have their stack traces
decorated on failure:

```java

@ExtendWith(ErrorDecorator.class)
class BaseTest {
}

class MyTest extends BaseTest {
  @Test
  void myTest() {
    // ...
  }
}
```

### JUnit 4

For JUnit 4, create a base test with a test rule, then all tests
inheriting `BaseTest` will have their stack traces
decorated on failure:

```java
public class BaseTest {
  @Rule
  public final ErrorDecorator errorDecorator = new ErrorDecorator();
}

public final class MyTest extends BaseTest {
  @Test
  public void myTest() {
    // ...
  }
}
```
