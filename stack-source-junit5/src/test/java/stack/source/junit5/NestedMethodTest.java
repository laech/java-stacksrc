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
  NestedMethodTest.AssertDecoration.class,
  ErrorDecorator.class
})
class NestedMethodTest {

  @Test
  void test() {
    doTest();
  }

  private void doTest() {
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    throw new AssertionError("test");
  }

  static class AssertDecoration implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(
      ExtensionContext context,
      Throwable e
    ) {
      String expected = join(
        lineSeparator(),
        "java.lang.AssertionError: test",
        "\tat stack.source.junit5.NestedMethodTest.doTest(NestedMethodTest.java:29)",
        "",
        "\t   25    private void doTest() {",
        "\t   26      assertEquals(1, 1);",
        "\t   27      assertEquals(1, 1);",
        "\t   28      assertEquals(1, 1);",
        "\t-> 29      throw new AssertionError(\"test\");",
        "\t   30    }",
        "",
        "",
        "\tat stack.source.junit5.NestedMethodTest.test(NestedMethodTest.java:22)",
        "",
        "\t   20    @Test",
        "\t   21    void test() {",
        "\t-> 22      doTest();",
        "\t   23    }",
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
