package nz.lae.stacksrc.junit5;

import static nz.lae.stacksrc.test.Assertions.assertStackTrace;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import nz.lae.stacksrc.DecoratedAssertionError;
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
nz.lae.stacksrc.DecoratedAssertionError:
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
      assertStackTrace(expected, (DecoratedAssertionError) e);
    }
  }
}
