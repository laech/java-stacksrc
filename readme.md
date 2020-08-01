# Stack Source

Decorates test failure stack traces with source code snippets to make them more helpful:

```
org.junit.ComparisonFailure: expected:<H[ello]!> but was:<H[i]!>
	at org.junit.Assert.assertEquals(Assert.java:115)
	at example.HelloTest.hello(HelloTest.java:16)

	   14      @Test
	   15      public void hello() {
	-> 16          assertEquals("Hello!", greet());
	   17      }

    ...
```

## Usage

Requires Java 8+.

### JUnit 5

[![Javadocs](https://www.javadoc.io/badge/com.gitlab.lae.stack.source/stack-source-junit5.svg)](https://www.javadoc.io/doc/com.gitlab.lae.stack.source/stack-source-junit5)

```java
/* Alternatively, run your tests with
 * -Djunit.jupiter.extensions.autodetection.enabled=true
 * then you don't need to use @ExtendWith(ErrorDecorator.class)
 */

@ExtendWith(ErrorDecorator.class)
class BaseTest {}

class MyTest extends BaseTest {
  @Test
  void myTest() {
    // ...
  }
}
```

### JUnit 4

[![Javadocs](https://www.javadoc.io/badge/com.gitlab.lae.stack.source/stack-source-junit4.svg)](https://www.javadoc.io/doc/com.gitlab.lae.stack.source/stack-source-junit4)

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

### Maven `pom.xml`

```xml
<project>
  <dependencies>

    <!--
      If you use JUnit 5
      You also need: https://junit.org/junit5/docs/current/user-guide/#running-tests-build-maven
    -->
    <dependency>
      <groupId>com.gitlab.lae.stack.source</groupId>
      <artifactId>stack-source-junit5</artifactId>
      <version>0.4.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>com.gitlab.lae.stack.source</groupId>
      <artifactId>stack-source-processor</artifactId>
      <version>0.4.2</version>
      <scope>test</scope>
    </dependency>

    <!--
      If you use JUnit 4
    -->
    <dependency>
      <groupId>com.gitlab.lae.stack.source</groupId>
      <artifactId>stack-source-junit4</artifactId>
      <version>0.4.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>com.gitlab.lae.stack.source</groupId>
      <artifactId>stack-source-processor</artifactId>
      <version>0.4.2</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <pluginManagement>
      <plugins>

        <!--
          If you distribute your tests as a jar file,
          exclude all local meta data files under
          stack-source/**
        -->
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-jar-plugin</artifactId>
          <version>3.1.0</version>
          <configuration>
          <excludes>
            <exclude>stack-source/**</exclude>
          </excludes>
          </configuration>
        </plugin>
      </plugins>
    </pluginManagement>
  </build>
</project>
```

### Gradle `build.gradle`

```groovy
dependencies {

  // For JUnit 4
  testAnnotationProcessor 'com.gitlab.lae.stack.source:stack-source-processor:0.4.2'
  testImplementation 'com.gitlab.lae.stack.source:stack-source-junit4:0.4.2'

  // For JUnit 5
  testAnnotationProcessor 'com.gitlab.lae.stack.source:stack-source-processor:0.4.2'
  testImplementation 'com.gitlab.lae.stack.source:stack-source-junit5:0.4.2'

}
```

### IntelliJ IDEA

Works with Maven and Gradle.

### Eclipse

Not supported because Eclipse uses its own compiler which is not supported by the Java Compiler Tree API.
