package nz.lae.stacksrc.junit5;

import static nz.lae.stacksrc.test.Assertions.assertStackTraceHasExpectedPrefix;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import nz.lae.stacksrc.DecoratedAssertionError;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

@ExtendWith({ErrorDecoratorNestedTest.AssertDecoration.class, ErrorDecorator.class})
class ErrorDecoratorNestedTest {

  @Nested
  class NestedCheck {
    @Test
    void decoratesFailure() {
      fail("testing failure");
    }
  }

  static class AssertDecoration implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable e) {
      var expected =
          """
org.opentest4j.AssertionFailedError: testing failure
	at org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:38)
	at org.junit.jupiter.api.Assertions.fail(Assertions.java:134)
	at nz.lae.stacksrc.junit5.ErrorDecoratorNestedTest$NestedCheck.decoratesFailure(ErrorDecoratorNestedTest.java:21)

	   19      @Test
	   20      void decoratesFailure() {
	-> 21        fail("testing failure");
	   22      }
	   23    }


""";
      assertEquals(DecoratedAssertionError.class, e.getClass());
      assertStackTraceHasExpectedPrefix(expected, (DecoratedAssertionError) e);
    }
  }
}
