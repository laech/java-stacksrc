# Stack Source

Decorates test failure stacks trace with source code snippets to make them more useful.

```
org.junit.ComparisonFailure: expected:<Bob h[ello]!> but was:<Bob h[i]!> expected:<Bob h[ello]!> but was:<Bob h[i]!>
        at org.junit.Assert.assertEquals(Assert.java:115)
        at org.junit.Assert.assertEquals(Assert.java:144)
        at bob.BobTest.helloBob(BobTest.java:16)

           14      @Test
           15      public void helloBob() {
        -> 16          assertEquals("Bob hello!", greet("Bob"));
           17      }

        ...
```

## Usage

```xml
<!-- JUnit 5 -->
<dependency>
  <groupId>com.gitlab.lae.stack.source</groupId>
  <artifactId>stack-source-junit5</artifactId>
  <version>0.2-beta5</version>
  <scope>test</scope>
</dependency>
```
```xml
<!-- JUnit 4 -->
<dependency>
  <groupId>com.gitlab.lae.stack.source</groupId>
  <artifactId>stack-source-junit4</artifactId>
  <version>0.2-beta5</version>
  <scope>test</scope>
</dependency>
```
