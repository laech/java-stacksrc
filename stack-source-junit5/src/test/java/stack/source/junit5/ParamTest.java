package stack.source.junit5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import static java.lang.Math.min;
import static java.lang.String.join;
import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static stack.source.internal.Throwables.getStackTraceAsString;

@ExtendWith({
  ParamTest.AssertDecoration.class,
  ErrorDecorator.class
})
class ParamTest {

  @Test
  void test(TestInfo info) {
    fail(info.getDisplayName());
  }

  static class AssertDecoration implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(
      ExtensionContext context,
      Throwable e
    ) {
      String expected = join(
        lineSeparator(),
        "org.opentest4j.AssertionFailedError: test(TestInfo)",
        "\tat org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:36)",
        "\tat org.junit.jupiter.api.Assertions.fail(Assertions.java:62)",
        "\tat stack.source.junit5.ParamTest.test(ParamTest.java:24)",
        "",
        "\t   22    @Test",
        "\t   23    void test(TestInfo info) {",
        "\t-> 24      fail(info.getDisplayName());",
        "\t   25    }",
        ""
      );
      assertEquals(DecoratedAssertionError.class, e.getClass());
      assertStackTrace(expected, e);
    }
  }

  private static void assertStackTrace(String expected, Throwable e) {
    String actual = getStackTraceAsString(e);
    actual = actual.substring(0, min(expected.length(), actual.length()));
    assertEquals(expected, actual);
  }
}
