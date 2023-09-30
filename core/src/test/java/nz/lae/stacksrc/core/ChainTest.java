package nz.lae.stacksrc.core;

import static java.lang.Math.min;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ChainTest {

  private void doThrow() {
    new ChainTest()
        .nothing1()
        .nothing2()
        .fail("what?")
        .nothing3()
        .fail("more?")
        .fail("and more?")
        .fail("and more more?");
  }

  private ChainTest fail(String message) {
    throw new AssertionError(message);
  }

  private ChainTest nothing1() {
    return this;
  }

  private ChainTest nothing2() {
    return this;
  }

  private ChainTest nothing3() {
    return this;
  }

  @Test
  void run() {
    var exception = assertThrows(AssertionError.class, this::doThrow);
    var expected =
        """
java.lang.AssertionError: what?
	at nz.lae.stacksrc.core.ChainTest.fail(ChainTest.java:23)

	   22    private ChainTest fail(String message) {
	-> 23      throw new AssertionError(message);
	   24    }


	at nz.lae.stacksrc.core.ChainTest.doThrow(ChainTest.java:15)

	   13          .nothing1()
	   14          .nothing2()
	-> 15          .fail("what?")
	   16          .nothing3()
	   17          .fail("more?")

""";
    assertStackTrace(expected, exception);
  }

  private static void assertStackTrace(String expected, Throwable e) {
    var actual = StackTraceDecorator.print(e);
    actual = actual.substring(0, min(expected.length(), actual.length()));
    assertEquals(expected, actual);
  }
}
