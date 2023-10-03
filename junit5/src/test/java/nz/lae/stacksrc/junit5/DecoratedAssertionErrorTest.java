package nz.lae.stacksrc.junit5;

import static org.junit.jupiter.api.Assertions.*;

import nz.lae.stacksrc.core.StackTraceDecorator;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

class DecoratedAssertionErrorTest {

  @Test
  void createInstanceWithoutExpectedAndActualValues() {
    var original = new AssertionFailedError("test");
    assertNull(original.getExpected());
    assertNull(original.getActual());

    var created = DecoratedAssertionError.create(original, StackTraceDecorator.create());
    assertEquals(original.getExpected(), created.getExpected());
    assertEquals(original.getActual(), created.getActual());
  }

  @Test
  void createInstanceWithExpectedAndActualValues() {
    var original = new AssertionFailedError("test", "expected", "actual");
    assertNotNull(original.getExpected());
    assertNotNull(original.getActual());

    var created = DecoratedAssertionError.create(original, StackTraceDecorator.create());
    assertEquals(original.getExpected(), created.getExpected());
    assertEquals(original.getActual(), created.getActual());
  }
}
