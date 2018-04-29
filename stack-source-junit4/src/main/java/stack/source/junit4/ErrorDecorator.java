package stack.source.junit4;

import org.junit.AssumptionViolatedException;
import org.junit.ComparisonFailure;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Decorates stack traces with source code snippets.
 * <p>
 * Example output:
 *
 * <pre>
 *
 * decorated org.junit.ComparisonFailure: expected:&lt;H[ello]!&gt; but was:&lt;H[i]!&gt;
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
 * <p>
 * Usage:
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
            public void evaluate() throws Throwable {
                ErrorDecorator.this.evaluate(base);
            }
        };
    }

    private void evaluate(Statement base) throws Throwable {
        try {
            base.evaluate();
        } catch (Throwable e) {
            if (e instanceof AssumptionViolatedException) {
                throw e;
            }
            if (e instanceof ComparisonFailure) {
                throw new DecoratedComparisonFailure((ComparisonFailure) e);
            }
            throw new DecoratedAssertionError(e);
        }
    }
}
