package stack.source.junit5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import static java.lang.Math.min;
import static java.lang.String.join;
import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static stack.source.internal.Throwables.getStackTraceAsString;

@ExtendWith({
  MultilineThrowTest.AssertDecoration.class,
  ErrorDecorator.class
})
class MultilineThrowTest {

  @Test
  void test() {
    throw new AssertionError(
      "hello world"
    );
  }

  static class AssertDecoration implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(
      ExtensionContext context,
      Throwable e
    ) {
      String expected = join(
        lineSeparator(),
        "java.lang.AssertionError: hello world",
        "\tat stack.source.junit5.MultilineThrowTest.test(MultilineThrowTest.java:22)",
        "",
        "\t-> 22      throw new AssertionError(",
        "\t   23        \"hello world\"",
        "\t   24      );",
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
