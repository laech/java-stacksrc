package nz.lae.stacksrc.junit5;

import static java.util.Objects.requireNonNull;

import com.google.auto.service.AutoService;
import nz.lae.stacksrc.core.StackTraceDecorator;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.opentest4j.IncompleteExecutionException;

/**
 * Decorates stack traces with source code snippets.
 *
 * <p>Example output:
 *
 * <pre>
 *
 * org.junit.ComparisonFailure: expected:&lt;H[ello]!&gt; but was:&lt;H[i]!&gt;
 *     at org.junit.Assert.assertEquals(Assert.java:115)
 *     at example.HelloTest.hello(HelloTest.java:16)
 *
 *        14      @Test
 *        15      public void hello() {
 *     -&gt; 16          assertEquals("Hello!", greet());
 *        17      }
 *
 * ...
 * </pre>
 *
 * <p>Usage:
 *
 * <pre>
 *
 * &#x40;ExtendWith(ErrorDecorator.class)
 * class BaseTest {}
 *
 * class MyTest extends BaseTest {
 *   &#x40;Test
 *   void myTest() {
 *     // ...
 *   }
 * }
 * </pre>
 *
 * <p>Alternatively, run your tests with {@code
 * -Djunit.jupiter.extensions.autodetection.enabled=true} instead of using annotations.
 */
@AutoService(Extension.class)
public final class ErrorDecorator implements TestExecutionExceptionHandler {

  private final StackTraceDecorator decorator;

  public ErrorDecorator() {
    this(StackTraceDecorator.create());
  }

  public ErrorDecorator(StackTraceDecorator decorator) {
    this.decorator = requireNonNull(decorator, "decorator");
  }

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable e) throws Throwable {
    if (e instanceof IncompleteExecutionException) {
      throw e;
    }
    throw new DecoratedAssertionError(decorator.decorate(e));
  }
}
