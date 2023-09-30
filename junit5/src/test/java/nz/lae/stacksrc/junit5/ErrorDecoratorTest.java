package nz.lae.stacksrc.junit5;

import static java.lang.Math.min;
import static nz.lae.stacksrc.core.Throwables.getStackTraceAsString;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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

  @Test
  void decoratesArrayFailure() {
    assertArrayEquals(new int[] {1}, new int[] {2});
  }

  static class AssertDecoration implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable e)
        throws Throwable {
      switch (context.getRequiredTestMethod().getName()) {
        case "decoratesFailure" -> assertEqualsFailure(e);
        case "decoratesArrayFailure" -> assertArrayEqualsFailure(e);
        default -> throw e;
      }
    }

    private void assertEqualsFailure(Throwable e) {
      var expected =
          """
nz.lae.stacksrc.junit5.DecoratedAssertionError:
org.opentest4j.AssertionFailedError: testing failure
	at org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:38)
	at org.junit.jupiter.api.Assertions.fail(Assertions.java:134)
	at nz.lae.stacksrc.junit5.ErrorDecoratorTest.decoratesFailure(ErrorDecoratorTest.java:19)

	   17    @Test
	   18    void decoratesFailure() {
	-> 19      fail("testing failure");
	   20    }

""";
      assertEquals(DecoratedAssertionError.class, e.getClass());
      assertStackTrace(expected, e);
    }

    private void assertArrayEqualsFailure(Throwable e) {
      var expected =
          """
nz.lae.stacksrc.junit5.DecoratedAssertionError:
org.opentest4j.AssertionFailedError: array contents differ at index [0], expected: <1> but was: <2>
	at org.junit.jupiter.api.AssertionFailureBuilder.build(AssertionFailureBuilder.java:151)
	at org.junit.jupiter.api.AssertionFailureBuilder.buildAndThrow(AssertionFailureBuilder.java:132)
	at org.junit.jupiter.api.AssertArrayEquals.failArraysNotEqual(AssertArrayEquals.java:440)
	at org.junit.jupiter.api.AssertArrayEquals.assertArrayEquals(AssertArrayEquals.java:241)
	at org.junit.jupiter.api.AssertArrayEquals.assertArrayEquals(AssertArrayEquals.java:87)
	at org.junit.jupiter.api.AssertArrayEquals.assertArrayEquals(AssertArrayEquals.java:83)
	at org.junit.jupiter.api.Assertions.assertArrayEquals(Assertions.java:1277)
	at nz.lae.stacksrc.junit5.ErrorDecoratorTest.decoratesArrayFailure(ErrorDecoratorTest.java:24)

	   22    @Test
	   23    void decoratesArrayFailure() {
	-> 24      assertArrayEquals(new int[] {1}, new int[] {2});
	   25    }

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
