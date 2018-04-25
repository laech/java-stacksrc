package stack.source.junit4;

import org.junit.AssumptionViolatedException;
import org.junit.ComparisonFailure;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

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
