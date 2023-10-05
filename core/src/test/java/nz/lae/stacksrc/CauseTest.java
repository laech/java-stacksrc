package nz.lae.stacksrc;

import static java.util.stream.Collectors.joining;
import static nz.lae.stacksrc.test.Assertions.assertStackTrace;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CauseTest {

  private void doThrow() {
    try {
      throw new IllegalArgumentException("test");
    } catch (IllegalArgumentException e) {
      throw new AssertionError("rethrown", e);
    }
  }

  @Test
  void run() {
    var exception = assertThrows(AssertionError.class, this::doThrow);
    var expected =
        """
java.lang.AssertionError: rethrown
	at nz.lae.stacksrc.CauseTest.doThrow(CauseTest.java:15)

	   13        throw new IllegalArgumentException("test");
	   14      } catch (IllegalArgumentException e) {
	-> 15        throw new AssertionError("rethrown", e);
	   16      }
	   17    }


	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:53)
	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:35)
	at org.junit.jupiter.api.Assertions.assertThrows(Assertions.java:3111)
	at nz.lae.stacksrc.CauseTest.run(CauseTest.java:21)

	   19    @Test
	   20    void run() {
	-> 21      var exception = assertThrows(AssertionError.class, this::doThrow);
	   22      var expected =
	   23          ""\"


Caused by: java.lang.IllegalArgumentException: test
	at nz.lae.stacksrc.CauseTest.doThrow(CauseTest.java:13)

	   11    private void doThrow() {
	   12      try {
	-> 13        throw new IllegalArgumentException("test");
	   14      } catch (IllegalArgumentException e) {
	   15        throw new AssertionError("rethrown", e);

    """;

    var actual =
        StackTraceDecorator.get()
            .decorate(exception)
            .lines()
            .filter(line -> !line.contains("java.base/"))
            .filter(line -> !line.contains("jdk.proxy1/"))
            .filter(line -> !line.contains("org.junit.platform"))
            .filter(line -> !line.contains("org.junit.jupiter.engine"))
            .filter(line -> !line.contains("org.gradle"))
            .filter(line -> !line.contains("\t..."))
            .collect(joining("\n"));

    assertStackTrace(expected, actual);
  }
}
