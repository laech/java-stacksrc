package nz.lae.stacksrc;

import static nz.lae.stacksrc.test.Assertions.assertStackTraceHasExpectedPrefix;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LambdaTest {

  private void doThrow() {
    String expected = "hi";
    lambda(
        () -> {
          assertTrue(true);
          throw new AssertionError(expected);
        });
  }

  private void lambda(Runnable code) {
    code.run();
  }

  @Test
  void run() {
    var exception = assertThrows(AssertionError.class, this::doThrow);
    var expected =
        """
java.lang.AssertionError: hi
	at nz.lae.stacksrc.LambdaTest.lambda$doThrow$0(LambdaTest.java:16)

	   14          () -> {
	   15            assertTrue(true);
	-> 16            throw new AssertionError(expected);
	   17          });
	   18    }


	at nz.lae.stacksrc.LambdaTest.lambda(LambdaTest.java:21)

	   20    private void lambda(Runnable code) {
	-> 21      code.run();
	   22    }

""";
    assertStackTraceHasExpectedPrefix(expected, StackTraceDecorator.get().decorate(exception));
  }
}
