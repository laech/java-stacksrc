package nz.lae.stacksrc.test;

import static java.lang.Math.min;
import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Assertions {
  private Assertions() {}

  public static void assertStackTrace(String expected, Throwable e) {
    assertStackTrace(expected, getStackTraceAsString(e));
  }

  public static void assertStackTrace(String expected, String actual) {
    expected = expected.replaceAll("\r?\n", lineSeparator());
    actual = actual.replaceAll("\r?\n", lineSeparator());
    actual = actual.substring(0, min(expected.length(), actual.length()));
    assertEquals(expected, actual);
  }

  private static String getStackTraceAsString(Throwable e) {
    var stringWriter = new StringWriter();
    var printWriter = new PrintWriter(stringWriter);
    e.printStackTrace(printWriter);
    printWriter.flush();
    return stringWriter.toString();
  }
}
