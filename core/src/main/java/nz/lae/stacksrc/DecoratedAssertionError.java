package nz.lae.stacksrc;

import java.io.PrintStream;
import java.io.PrintWriter;
import javax.annotation.Nullable;

/**
 * Wraps an exception and prints stack trace with code snippets.
 *
 * <p>To obtain the original exception, use {@link #getOriginal()}.
 */
public final class DecoratedAssertionError extends AssertionError {

  private final Throwable original;
  private final String decoratedStackTrace;

  public DecoratedAssertionError(Throwable original) {
    this.original = original;
    this.decoratedStackTrace = StackTraceDecorator.get().decorate(original);
    setStackTrace(new StackTraceElement[0]);
  }

  @Nullable
  @Override
  public String getMessage() {
    // Override this instead of calling the super(message) constructor, as super(null) will create
    // the "null" string instead of actually being null
    return getOriginal().getMessage();
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
    return decoratedStackTrace;
  }
}
