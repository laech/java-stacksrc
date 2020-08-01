package stack.source.junit5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import static java.lang.Math.min;
import static java.lang.String.join;
import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static stack.source.internal.Throwables.getStackTraceAsString;

@ExtendWith({
  LambdaTest.AssertDecoration.class,
  ErrorDecorator.class
})
class LambdaTest {

  @Test
  void test() {
    String expected = "hi";
    lambda(() -> {
      assertTrue(true);
      throw new AssertionError(expected);
    });
  }

  private void lambda(Runnable code) {
    code.run();
  }

  static class AssertDecoration implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(
      ExtensionContext context,
      Throwable e
    ) {
      String expected = join(
        lineSeparator(),
        "java.lang.AssertionError: hi",
        "\tat stack.source.junit5.LambdaTest.lambda$test$0(LambdaTest.java:26)",
        "",
        "\t   24      lambda(() -> {",
        "\t   25        assertTrue(true);",
        "\t-> 26        throw new AssertionError(expected);",
        "\t   27      });",
        "",
        "",
        "\tat stack.source.junit5.LambdaTest.lambda(LambdaTest.java:31)",
        "",
        "\t   30    private void lambda(Runnable code) {",
        "\t-> 31      code.run();",
        "\t   32    }",
        "",
        "",
        "\tat stack.source.junit5.LambdaTest.test(LambdaTest.java:24)",
        "\tat "
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
