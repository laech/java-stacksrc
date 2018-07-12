package stack.source.junit5;

import org.opentest4j.AssertionFailedError;
import stack.source.internal.Decorator;

import java.io.PrintStream;
import java.io.PrintWriter;

final class DecoratedAssertionFailedError extends AssertionFailedError {

    /*
     * Decorates an original AssertionFailedError, by being a AssertionFailedError
     * as well this just works with existing IDEs features such as those in
     * IntelliJ. IntelliJ provides diff like viewer when test are failed
     * due to comparison failures, it's quite useful.
     */

    private final Throwable src;

    private DecoratedAssertionFailedError(String message, Throwable src) {
        super(message, src.getCause());
        this.src = src;
    }

    private DecoratedAssertionFailedError(
            String message, Object expected, Object actual, Throwable src) {
        super(message, expected, actual, src.getCause());
        this.src = src;
    }

    static DecoratedAssertionFailedError create(Throwable src) {

        // The two constructors have different behaviour due to
        // parent constructor org.opentest4j.DecoratedAssertionFailedError

        DecoratedAssertionFailedError result;
        if (src instanceof AssertionFailedError
                && ((AssertionFailedError) src).getExpected() != null
                && ((AssertionFailedError) src).getActual() != null) {

            result = new DecoratedAssertionFailedError(
                    src.toString(),
                    ((AssertionFailedError) src).getExpected().getValue(),
                    ((AssertionFailedError) src).getActual().getValue(),
                    src
            );
        } else {
            result = new DecoratedAssertionFailedError(
                    src.toString(),
                    src
            );
        }

        result.setStackTrace(src.getStackTrace());
        for (Throwable sp : src.getSuppressed()) {
            result.addSuppressed(sp);
        }
        return result;
    }

    @Override
    public void printStackTrace(PrintStream s) {
        Decorator.printSafely(src, s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        Decorator.printSafely(src, s);
    }
}
