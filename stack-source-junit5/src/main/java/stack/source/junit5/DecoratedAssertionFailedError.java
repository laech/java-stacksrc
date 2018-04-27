package stack.source.junit5;

import org.opentest4j.AssertionFailedError;
import stack.source.internal.Decorator;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

final class DecoratedAssertionFailedError extends AssertionFailedError {

    /*
     * Decorates an original AssertionFailedError, by being a AssertionFailedError
     * as well this just works with existing IDEs features such as those in
     * IntelliJ. IntelliJ provides diff like viewer when test are failed
     * due to comparison failures, it's quite useful.
     */

    private DecoratedAssertionFailedError(String message, Throwable cause) {
        super(message, cause);
    }

    private DecoratedAssertionFailedError(
            String message, Object expected, Object actual, Throwable cause) {
        super(message, expected, actual, cause);
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
                    src.getCause()
            );
        } else {
            result = new DecoratedAssertionFailedError(
                    src.toString(),
                    src.getCause()
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
        try {
            new Decorator(this).print(s);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        try {
            new Decorator(this).print(s);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String toString() {
        return "decorated " + getMessage();
    }
}
