package stack.source.junit5;

import static java.lang.Math.min;
import static java.lang.String.join;
import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static stack.source.internal.Throwables.getStackTraceAsString;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

@ExtendWith({
  MultiLongChainsTest.AssertDecoration.class,
  ErrorDecorator.class
})
class MultiLongChainsTest {

  @Test
  @SuppressWarnings("unused")
  void test() {

    var helper1 = new Helper();
    var helper2 = helper1
      .test("x")
      .test("x")
      .test("x");

    test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x");

    test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x")
      .test("x");
  }

  private MultiLongChainsTest test(
    @SuppressWarnings({"SameParameterValue", "unused"}) String msg
  ) {
    return this;
  }

  private static class Helper {
    private Helper test(
      @SuppressWarnings({"SameParameterValue", "unused"}) String msg
    ) {
      throw new AssertionError("bob");
    }
  }

  static class AssertDecoration implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(
      ExtensionContext context,
      Throwable e
    ) {
      var expected = join(
        lineSeparator(),
        "java.lang.AssertionError: bob",
        "\tat stack.source.junit5.MultiLongChainsTest$Helper.test(MultiLongChainsTest.java:71)",
        "",
        "\t   69        @SuppressWarnings({\"SameParameterValue\", \"unused\"}) String msg",
        "\t   70      ) {",
        "\t-> 71        throw new AssertionError(\"bob\");",
        "\t   72      }",
        "\t   73    }",
        "",
        "",
        "\tat stack.source.junit5.MultiLongChainsTest.test(MultiLongChainsTest.java:26)",
        "",
        "\t   24      var helper1 = new Helper();",
        "\t   25      var helper2 = helper1",
        "\t-> 26        .test(\"x\")",
        "\t   27        .test(\"x\")",
        "\t   28        .test(\"x\");",
        "",
        ""
      );
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
