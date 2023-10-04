package nz.lae.stacksrc.junit4;

import java.io.PrintStream;
import java.io.PrintWriter;
import nz.lae.stacksrc.core.StackTraceDecorator;
import org.junit.ComparisonFailure;

public final class DecoratedComparisonFailure extends ComparisonFailure {

  // This extends from ComparisonFailure and keeps its structure, it also has the benefit that
  // IntelliJ will recognise this and show its '<Click to see difference>' feature in its test
  // failure window for any equality failures.

  private final ComparisonFailure original;
  private final String decoratedStackTrace;

  public DecoratedComparisonFailure(ComparisonFailure original, StackTraceDecorator decorator) {
    super(null, original.getExpected(), original.getActual());
    this.original = original;
    this.decoratedStackTrace = decorator.decorate(original);
    setStackTrace(new StackTraceElement[0]);
  }

  /** Gets the original throwable being wrapped. */
  public Throwable getOriginal() {
    return original;
  }

  @Override
  public String getMessage() {
    return original.getMessage();
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
