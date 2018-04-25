package stack.source.junit4;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runners.model.Statement;

import static java.lang.Math.min;
import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;
import static stack.source.internal.Throwables.getStackTraceAsString;

public final class ErrorDecoratorTest {

    @Rule
    public final RuleChain r = RuleChain
            .outerRule((base, description) -> new AssertStatement(base))
            .around(new ErrorDecorator());

    private static class AssertStatement extends Statement {

        private final Statement base;

        AssertStatement(Statement base) {
            this.base = requireNonNull(base);
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                base.evaluate();
            } catch (DecoratedAssertionError e) {
                assertMessage(e);
            }
        }

        private void assertMessage(DecoratedAssertionError e) {
            String expected = String.join(getProperty("line.separator"),
                    "stack.source.junit4.DecoratedAssertionError: testing failure",
                    "\tat org.junit.Assert.fail(Assert.java:88)",
                    "\tat stack.source.junit4.Fail.run(Fail.java:8)",
                    "",
                    "\t-> 8          fail(\"testing failure\");",
                    ""
            );
            String actual = getStackTraceAsString(e);
            actual = actual.substring(0, min(expected.length(), actual.length()));
            assertEquals(expected, actual);
        }
    }

    @Test
    public void decoratesError() {
        new Fail().run();
    }

    @Test
    public void assumeApiPassThrough() {
        assumeThat(false, is(true));
    }

    @Test
    @Ignore
    public void ignoreApiPassThrough() {
    }
}
