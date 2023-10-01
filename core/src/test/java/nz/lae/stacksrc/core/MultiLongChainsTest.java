package nz.lae.stacksrc.core;

import static nz.lae.stacksrc.test.Assertions.assertStackTrace;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MultiLongChainsTest {

  private void doThrow() {

    var helper1 = new Helper();
    helper1.test("x").test("x").test("x");

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

  private MultiLongChainsTest test(@SuppressWarnings({"SameParameterValue", "unused"}) String msg) {
    return this;
  }

  private static class Helper {
    private Helper test(@SuppressWarnings({"SameParameterValue", "unused"}) String msg) {
      throw new AssertionError("bob");
    }
  }

  @Test
  void run() {
    var exception = assertThrows(AssertionError.class, this::doThrow);
    var expected =
        """
java.lang.AssertionError: bob
	at nz.lae.stacksrc.core.MultiLongChainsTest$Helper.test(MultiLongChainsTest.java:52)

	   50    private static class Helper {
	   51      private Helper test(@SuppressWarnings({"SameParameterValue", "unused"}) String msg) {
	-> 52        throw new AssertionError("bob");
	   53      }
	   54    }


	at nz.lae.stacksrc.core.MultiLongChainsTest.doThrow(MultiLongChainsTest.java:13)

	   12      var helper1 = new Helper();
	-> 13      helper1.test("x").test("x").test("x");
	   14
	   15      test("x")


	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:53)
	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:35)
	at org.junit.jupiter.api.Assertions.assertThrows(Assertions.java:3111)
	at nz.lae.stacksrc.core.MultiLongChainsTest.run(MultiLongChainsTest.java:58)

	   56    @Test
	   57    void run() {
	-> 58      var exception = assertThrows(AssertionError.class, this::doThrow);
	   59      var expected =
	   60          ""\"

""";
    assertStackTrace(expected, StackTraceDecorator.print(exception));
  }
}
