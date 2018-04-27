# Stack Source

Decorates test failure stacks trace with source code snippets to make them more helpful.

```
decorated org.junit.ComparisonFailure: bob expected:<H[ello]!> but was:<H[i]!>
	at org.junit.Assert.assertEquals(Assert.java:115)
	at example.HelloTest.hello(HelloTest.java:16)

	   14      @Test
	   15      public void hello() {
	-> 16          assertEquals("bob", "Hello!", greet());
	   17      }


    ...
```

## Usage

### JUnit 5

```xml
<dependency>
  <groupId>com.gitlab.lae.stack.source</groupId>
  <artifactId>stack-source-junit5</artifactId>
  <version>0.2-beta5</version>
  <scope>test</scope>
</dependency>
```

### JUnit 4

```xml
<dependency>
  <groupId>com.gitlab.lae.stack.source</groupId>
  <artifactId>stack-source-junit4</artifactId>
  <version>0.2-beta5</version>
  <scope>test</scope>
</dependency>
```
