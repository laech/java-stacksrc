package stack.source.junit4;

import java.io.PrintStream;
import java.io.PrintWriter;
import stack.source.internal.Decorator;

final class DecoratedAssertionError extends AssertionError {

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
