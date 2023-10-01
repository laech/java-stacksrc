package nz.lae.stacksrc.core;

import static java.lang.Math.min;
import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
  private Assertions() {}

  public static void assertStackTrace(String expected, Throwable e) {
    var actual = StackTraceDecorator.print(e);
    actual = actual.substring(0, min(expected.length(), actual.length()));
    expected = expected.replaceAll("\n\r?", lineSeparator());
    assertEquals(expected, actual);
  }
}
