package stack.source.junit5;

import org.opentest4j.AssertionFailedError;
import stack.source.internal.Decorator;

import java.io.PrintStream;
import java.io.PrintWriter;

final class DecoratedAssertionError extends AssertionFailedError {

  private final Throwable original;

  DecoratedAssertionError(Throwable original) {
    super(Decorator.print(original));
    this.original = original;
    setStackTrace(new StackTraceElement[0]);
  }

  @Override
  public void printStackTrace(PrintWriter out) {
    out.println(getMessage());
  }

  @Override
  public void printStackTrace(PrintStream out) {
    out.println(getMessage());
  }

  @Override
  public String toString() {
    return getClass().getName() + ": " + original.getClass().getName();
  }
}
