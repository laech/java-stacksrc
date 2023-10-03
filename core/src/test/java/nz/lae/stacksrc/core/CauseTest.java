package nz.lae.stacksrc.core;

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
	at nz.lae.stacksrc.core.CauseTest.doThrow(CauseTest.java:14)

	   12        throw new IllegalArgumentException("test");
	   13      } catch (IllegalArgumentException e) {
	-> 14        throw new AssertionError("rethrown", e);
	   15      }
	   16    }


	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:53)
	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:35)
	at org.junit.jupiter.api.Assertions.assertThrows(Assertions.java:3111)
	at nz.lae.stacksrc.core.CauseTest.run(CauseTest.java:20)

	   18    @Test
	   19    void run() {
	-> 20      var exception = assertThrows(AssertionError.class, this::doThrow);
	   21      var expected =
	   22          \"""

    """;
    assertStackTrace(expected, StackTraceDecorator.create().decorate(exception));
  }
}
