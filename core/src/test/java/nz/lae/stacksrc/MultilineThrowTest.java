package nz.lae.stacksrc;

import static nz.lae.stacksrc.test.Assertions.assertStackTrace;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MultilineThrowTest {

  private void doThrow() {
    throw new AssertionError( //
        "hello world" //
        );
  }

  @Test
  void run() {
    var exception = assertThrows(AssertionError.class, this::doThrow);
    var expected =
        """
java.lang.AssertionError: hello world
	at nz.lae.stacksrc.MultilineThrowTest.doThrow(MultilineThrowTest.java:11)

	   10    private void doThrow() {
	-> 11      throw new AssertionError( //
	   12          "hello world" //
	   13          );

""";
    assertStackTrace(expected, StackTraceDecorator.get().decorate(exception));
  }
}
