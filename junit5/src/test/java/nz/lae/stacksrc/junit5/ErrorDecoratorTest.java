package nz.lae.stacksrc.junit5;

import static java.lang.Math.min;
import static nz.lae.stacksrc.core.Throwables.getStackTraceAsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

@ExtendWith({ErrorDecoratorTest.AssertDecoration.class, ErrorDecorator.class})
class ErrorDecoratorTest {

  @Test
  void decoratesFailure() {
    fail("testing failure");
  }

  static class AssertDecoration implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable e) {
      var expected =
          """
nz.lae.stacksrc.junit5.DecoratedAssertionError:
org.opentest4j.AssertionFailedError: testing failure
	at org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:38)
	at org.junit.jupiter.api.Assertions.fail(Assertions.java:134)
	at nz.lae.stacksrc.junit5.ErrorDecoratorTest.decoratesFailure(ErrorDecoratorTest.java:18)

	   16    @Test
	   17    void decoratesFailure() {
	-> 18      fail("testing failure");
	   19    }

""";
      assertEquals(DecoratedAssertionError.class, e.getClass());
      assertStackTrace(expected, e);
    }
  }

  private static void assertStackTrace(String expected, Throwable e) {
    var actual = getStackTraceAsString(e);
    actual = actual.substring(0, min(expected.length(), actual.length()));
    assertEquals(expected, actual);
  }
}
