package nz.lae.stacksrc;

import static nz.lae.stacksrc.test.Assertions.assertStackTraceHasExpectedPrefix;
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
	at nz.lae.stacksrc.ChainTest.fail(ChainTest.java:22)

	   21    private ChainTest fail(String message) {
	-> 22      throw new AssertionError(message);
	   23    }


	at nz.lae.stacksrc.ChainTest.doThrow(ChainTest.java:14)

	   12          .nothing1()
	   13          .nothing2()
	-> 14          .fail("what?")
	   15          .nothing3()
	   16          .fail("more?")

""";
    assertStackTraceHasExpectedPrefix(expected, StackTraceDecorator.get().decorate(exception));
  }
}
