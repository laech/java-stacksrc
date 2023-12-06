package nz.lae.stacksrc.junit5;

import static nz.lae.stacksrc.Throwables.pruneStackTrace;

import nz.lae.stacksrc.DecoratedAssertionError;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.opentest4j.IncompleteExecutionException;

/**
 * An extension for catching test errors and wrapping them with {@link DecoratedAssertionError} for
 * decorating stack traces with source code snippets.
 *
 * <p>Example output:
 *
 * <pre>
 *
 * org.opentest4j.AssertionFailedError: expected: &lt;Hello!&gt; but was: &lt;Hi!&gt;
 *     ...
 *     at example.HelloTest.hello(HelloTest.java:16)
 *
 *        14      @Test
 *        15      public void hello() {
 *     -&gt; 16          assertEquals("Hello!", greet());
 *        17      }
 *
 * </pre>
 *
 * <p>Example usage:
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
 * Alternatively, instead of using {@link ExtendWith}, you can enable JUnit's <a
 * href="https://junit.org/junit5/docs/current/user-guide/#extensions-registration-automatic-enabling">automatic
 * extension detection</a> by setting the system property {@code
 * junit.jupiter.extensions.autodetection.enabled=true}, then this extension will be automatically
 * applied.
 *
 * @see <a
 *     href="https://junit.org/junit5/docs/current/user-guide/#extensions-registration">Registering
 *     Extensions</a>
 */
public final class ErrorDecorator implements TestExecutionExceptionHandler {

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
      throws Throwable {
    if (throwable instanceof IncompleteExecutionException
        || throwable instanceof DecoratedAssertionError) {
      throw throwable;
    }

    // https://junit.org/junit5/docs/current/user-guide/#stacktrace-pruning
    var pruneStackTrace =
        context
            .getConfigurationParameter("junit.platform.stacktrace.pruning.enabled")
            .map(Boolean::parseBoolean)
            .orElse(true);

    if (pruneStackTrace) {
      context.getTestClass().ifPresent(testClass -> pruneStackTrace(throwable, testClass));
    }

    throw new DecoratedAssertionError(throwable);
  }
}
