package nz.lae.stacksrc.core;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class Throwables {
  private Throwables() {}

  public static String getStackTraceAsString(Throwable e) {
    var stringWriter = new StringWriter();
    var printWriter = new PrintWriter(stringWriter);
    e.printStackTrace(printWriter);
    printWriter.flush();
    return stringWriter.toString();
  }
}
