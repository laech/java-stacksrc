package nz.lae.stacksrc;

import java.io.PrintStream;
import java.io.PrintWriter;

public final class DecoratedAssertionError extends AssertionError {

  private final Throwable original;
  private final String decoratedStackTrace;

  public DecoratedAssertionError(Throwable original, StackTraceDecorator decorator) {
    super(original.getMessage());
    this.original = original;
    this.decoratedStackTrace = decorator.decorate(original);
    setStackTrace(new StackTraceElement[0]);
  }

  /** Gets the original throwable being wrapped. */
  public Throwable getOriginal() {
    return original;
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
    return String.format("%s:%n%s", getClass().getName(), decoratedStackTrace);
  }
}
