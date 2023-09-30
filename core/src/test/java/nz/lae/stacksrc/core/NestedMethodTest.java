package nz.lae.stacksrc.core;

import static nz.lae.stacksrc.core.Assertions.assertStackTrace;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NestedMethodTest {

  private void doThrow() {
    doTest();
  }

  @SuppressWarnings("EqualsWithItself")
  private void doTest() {
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
	at nz.lae.stacksrc.core.NestedMethodTest.doTest(NestedMethodTest.java:20)

	   18      assertEquals(1, 1);
	   19      assertEquals(1, 1);
	-> 20      throw new AssertionError("test");
	   21    }


	at nz.lae.stacksrc.core.NestedMethodTest.doThrow(NestedMethodTest.java:12)

	   11    private void doThrow() {
	-> 12      doTest();
	   13    }

""";
    assertStackTrace(expected, exception);
  }
}
