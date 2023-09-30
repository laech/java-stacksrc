package nz.lae.stacksrc.junit5;

import static java.lang.Math.min;
import static java.lang.System.lineSeparator;
import static nz.lae.stacksrc.core.Throwables.getStackTraceAsString;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Disabled;
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

  @Test
  void assumeApiPassThrough() {
    assumeTrue(() -> false);
  }

  @Test
  @Disabled
  void disabledApiPassThrough() {}

  static class AssertDecoration implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable e)
        throws Throwable {

      switch (context.getRequiredTestMethod().getName()) {
        case "decoratesFailure":
          assertEqualsFailure(e);
          break;
        case "decoratesArrayFailure":
          assertArrayEqualsFailure(e);
          break;
        default:
          throw e;
      }
    }

    private void assertEqualsFailure(Throwable e) {
      var expected =
          String.join(
              lineSeparator(),
              "nz.lae.stacksrc.junit5.DecoratedAssertionError:",
              "org.opentest4j.AssertionFailedError: testing failure",
              "\tat org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:38)",
              "\tat org.junit.jupiter.api.Assertions.fail(Assertions.java:134)",
              "\tat nz.lae.stacksrc.junit5.ErrorDecoratorTest.decoratesFailure(ErrorDecoratorTest.java:22)",
              "",
              "\t   20    @Test",
              "\t   21    void decoratesFailure() {",
              "\t-> 22      fail(\"testing failure\");",
              "\t   23    }",
              "");
      assertEquals(DecoratedAssertionError.class, e.getClass());
      assertStackTrace(expected, e);
    }

    private void assertArrayEqualsFailure(Throwable e) {
      var expected =
          String.join(
              lineSeparator(),
              "nz.lae.stacksrc.junit5.DecoratedAssertionError:",
              "org.opentest4j.AssertionFailedError: array contents differ at index [0], expected: <1> but was: <2>",
              "\tat org.junit.jupiter.api.AssertionFailureBuilder.build(AssertionFailureBuilder.java:151)",
              "\tat org.junit.jupiter.api.AssertionFailureBuilder.buildAndThrow(AssertionFailureBuilder.java:132)",
              "\tat org.junit.jupiter.api.AssertArrayEquals.failArraysNotEqual(AssertArrayEquals.java:440)",
              "\tat org.junit.jupiter.api.AssertArrayEquals.assertArrayEquals(AssertArrayEquals.java:241)",
              "\tat org.junit.jupiter.api.AssertArrayEquals.assertArrayEquals(AssertArrayEquals.java:87)",
              "\tat org.junit.jupiter.api.AssertArrayEquals.assertArrayEquals(AssertArrayEquals.java:83)",
              "\tat org.junit.jupiter.api.Assertions.assertArrayEquals(Assertions.java:1277)",
              "\tat nz.lae.stacksrc.junit5.ErrorDecoratorTest.decoratesArrayFailure(ErrorDecoratorTest.java:27)",
              "",
              "\t   25    @Test",
              "\t   26    void decoratesArrayFailure() {",
              "\t-> 27      assertArrayEquals(new int[] {1}, new int[] {2});",
              "\t   28    }",
              "");
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
