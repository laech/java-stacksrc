# Stack Source

Decorates test failure stacks trace with source code snippets to make them more helpful.

```
decorated org.junit.ComparisonFailure: expected:<H[ello]!> but was:<H[i]!>
	at org.junit.Assert.assertEquals(Assert.java:115)
	at example.HelloTest.hello(HelloTest.java:16)

	   14      @Test
	   15      public void hello() {
	-> 16          assertEquals("Hello!", greet());
	   17      }

    ...
```

## Usage

### Maven `pom.xml`

```xml
<project>
  <dependencies>

    <!--
      If you use JUnit 5
    -->
    <dependency>
      <groupId>com.gitlab.lae.stack.source</groupId>
      <artifactId>stack-source-junit5</artifactId>
      <version>0.2-beta5</version>
      <scope>test</scope>
    </dependency>

    <!--
      If you use JUnit 4
    -->
    <dependency>
      <groupId>com.gitlab.lae.stack.source</groupId>
      <artifactId>stack-source-junit4</artifactId>
      <version>0.2-beta5</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <pluginManagement>
      <plugins>

        <!--
          Set <trimStackTrace> to false to prevent trimming
          of stack traces, therefore allowing decorated
          stack traces to be shown under build servers
          such as Jenkins. 
        -->
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-surefire-plugin</artifactId>
          <version>2.19.1</version>
          <configuration>
            <trimStackTrace>false</trimStackTrace>
          </configuration>
        </plugin>

        <!--
          If you distribute your tests as a jar file,
          exclude all local meta data files under
          stack-source/**
        -->
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-jar-plugin</artifactId>
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

### JUnit 5

```java
import stack.source.junit5.ErrorDecorator;

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

```java
import stack.source.junit4.ErrorDecorator;

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
