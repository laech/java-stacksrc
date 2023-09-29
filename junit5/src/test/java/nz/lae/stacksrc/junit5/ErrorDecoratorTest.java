package nz.lae.stacksrc.junit5;

import static java.lang.Math.min;
import static java.lang.System.lineSeparator;
import static nz.lae.stacksrc.core.Throwables.getStackTraceAsString;
import static org.junit.jupiter.api.Assertions.*;
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
              "\tat org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:36)",
              "\tat org.junit.jupiter.api.Assertions.fail(Assertions.java:62)",
              "\tat nz.lae.stacksrc.junit5.ErrorDecoratorTest.decoratesFailure(ErrorDecoratorTest.java:20)",
              "",
              "\t   18    @Test",
              "\t   19    void decoratesFailure() {",
              "\t-> 20      fail(\"testing failure\");",
              "\t   21    }",
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
              "\tat org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:36)",
              "\tat org.junit.jupiter.api.AssertArrayEquals.failArraysNotEqual(AssertArrayEquals.java:434)",
              "\tat org.junit.jupiter.api.AssertArrayEquals.assertArrayEquals(AssertArrayEquals.java:246)",
              "\tat org.junit.jupiter.api.AssertArrayEquals.assertArrayEquals(AssertArrayEquals.java:92)",
              "\tat org.junit.jupiter.api.AssertArrayEquals.assertArrayEquals(AssertArrayEquals.java:88)",
              "\tat org.junit.jupiter.api.Assertions.assertArrayEquals(Assertions.java:622)",
              "\tat nz.lae.stacksrc.junit5.ErrorDecoratorTest.decoratesArrayFailure(ErrorDecoratorTest.java:25)",
              "",
              "\t   23    @Test",
              "\t   24    void decoratesArrayFailure() {",
              "\t-> 25      assertArrayEquals(new int[] {1}, new int[] {2});",
              "\t   26    }",
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
