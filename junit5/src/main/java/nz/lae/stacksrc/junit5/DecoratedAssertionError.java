package nz.lae.stacksrc.junit5;

import java.io.PrintStream;
import java.io.PrintWriter;
import org.opentest4j.AssertionFailedError;

final class DecoratedAssertionError extends AssertionFailedError {

  DecoratedAssertionError(String stackTrace) {
    super(stackTrace);
    setStackTrace(new StackTraceElement[0]);
  }

  @Override
  public void printStackTrace(PrintWriter out) {
    out.println(this);
  }

  @Override
  public void printStackTrace(PrintStream out) {
    out.println(this);
  }

  @Override
  public String toString() {
    return String.format("%s:%n%s", getClass().getName(), getMessage());
  }
}
