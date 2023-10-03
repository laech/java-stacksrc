package nz.lae.stacksrc.junit5;

import static java.util.Objects.requireNonNull;

import java.io.PrintStream;
import java.io.PrintWriter;
import nz.lae.stacksrc.core.StackTraceDecorator;
import org.opentest4j.AssertionFailedError;

public final class DecoratedAssertionError extends AssertionFailedError {

  private final Throwable original;
  private final String decoratedStackTrace;

  private DecoratedAssertionError(Throwable original, StackTraceDecorator decorator) {
    super(original.getMessage());
    this.original = original;
    this.decoratedStackTrace = decorator.decorate(original);
    setStackTrace(new StackTraceElement[0]);
    // Note we can't just call this(original, decorator, null, null) as that will cause super to
    // create a ValueWrapper for both containing nulls, but we want no wrappers at all.
  }

  private DecoratedAssertionError(
      Throwable original, StackTraceDecorator decorator, Object expected, Object actual) {
    super(original.getMessage(), expected, actual);
    this.original = original;
    this.decoratedStackTrace = decorator.decorate(original);
    setStackTrace(new StackTraceElement[0]);
  }

  public static DecoratedAssertionError create(Throwable original, StackTraceDecorator decorator) {
    requireNonNull(original, "original");
    requireNonNull(decorator, "decorator");

    // We copy the expected and actual values from the original, this has the benefit that IntelliJ
    // will recognise this and show its '<Click to see difference>' feature in its test failure
    // window for any equality failures.

    if (original instanceof AssertionFailedError) {
      var fail = (AssertionFailedError) original;
      if (fail.isExpectedDefined() && fail.isActualDefined()) {
        return new DecoratedAssertionError(
            original, decorator, fail.getExpected(), fail.getActual());
      }
    }
    return new DecoratedAssertionError(original, decorator);
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
