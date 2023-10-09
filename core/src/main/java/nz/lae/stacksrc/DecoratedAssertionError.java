package nz.lae.stacksrc;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Wraps an exception and prints stack trace with code snippets.
 *
 * <p>To obtain the original exception, use {@link #getOriginal()}.
 */
public final class DecoratedAssertionError extends AssertionError {

  private final Throwable original;
  private final String decoratedStackTrace;

  public DecoratedAssertionError(Throwable original) {
    this(original, null);
  }

  /**
   * @param pruneStackTraceKeepFromClass if not null, will prune the stack traces, keeping only
   *     elements that are called directly or indirectly by this class
   */
  public DecoratedAssertionError(Throwable original, Class<?> pruneStackTraceKeepFromClass) {
    super(original.getMessage());
    this.original = original;
    this.decoratedStackTrace =
        StackTraceDecorator.get().decorate(original, pruneStackTraceKeepFromClass);
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
    return decoratedStackTrace;
  }
}
