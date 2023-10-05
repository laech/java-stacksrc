package nz.lae.stacksrc;

import static nz.lae.stacksrc.test.Assertions.assertStackTrace;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LongMethodTest {

  @SuppressWarnings("EqualsWithItself")
  private void doThrow() {
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    assertEquals(1, 1);
    throw new AssertionError("test");
  }

  @Test
  void run() {
    var exception = assertThrows(AssertionError.class, this::doThrow);
    var expected =
        """
java.lang.AssertionError: test
	at nz.lae.stacksrc.LongMethodTest.doThrow(LongMethodTest.java:39)

	   37      assertEquals(1, 1);
	   38      assertEquals(1, 1);
	-> 39      throw new AssertionError("test");
	   40    }

""";
    assertStackTrace(expected, StackTraceDecorator.create().decorate(exception));
  }
}
