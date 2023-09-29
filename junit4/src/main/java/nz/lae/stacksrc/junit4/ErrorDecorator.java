package nz.lae.stacksrc.junit4;

import org.junit.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

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
 * public class BaseTest {
 *   &#x40;Rule
 *   public final ErrorDecorator errorDecorator = new ErrorDecorator();
 * }
 *
 * public final class MyTest extends BaseTest {
 *   &#x40;Test
 *   public void myTest() {
 *     // ...
 *   }
 * }
 * </pre>
 */
public final class ErrorDecorator implements TestRule {

  @Override
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() {
        ErrorDecorator.this.evaluate(base);
      }
    };
  }

  private void evaluate(Statement base) {
    try {
      base.evaluate();
    } catch (AssumptionViolatedException e) {
      throw e;
    } catch (Throwable e) {
      throw new DecoratedAssertionError(e);
    }
  }
}
