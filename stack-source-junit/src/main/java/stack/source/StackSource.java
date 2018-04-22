package stack.source;

import org.junit.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import stack.source.internal.Throwables;

import java.io.IOException;
import java.util.HashMap;

import static java.lang.System.getProperty;

public final class StackSource implements TestRule {

    private static final String LINE_SEPARATOR = getProperty("line.separator");

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (Throwable e) {
                    if (e instanceof AssumptionViolatedException) {
                        throw new AssumptionViolation(e);
                    } else {
                        throw new Failure(e);
                    }
                }
            }
        };
    }

    private static final class AssumptionViolation
            extends AssumptionViolatedException {

        AssumptionViolation(Throwable e) {
            super(formatStackTrace(e));
            setStackTrace(new StackTraceElement[0]);
        }
    }

    private static final class Failure extends AssertionError {

        Failure(Throwable cause) {
            super(formatStackTrace(cause));
            setStackTrace(new StackTraceElement[0]);
        }
    }

    public static String formatStackTrace(Throwable cause) {
        StringBuilder builder = new StringBuilder(LINE_SEPARATOR);
        try {
            Throwables.printStackTrace(cause, builder, true);
            // TODO return original stacktrace on error
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
