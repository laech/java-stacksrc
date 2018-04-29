package stack.source.junit4;

import org.junit.ComparisonFailure;
import stack.source.internal.Decorator;

import java.io.PrintStream;
import java.io.PrintWriter;

final class DecoratedComparisonFailure extends ComparisonFailure {

    /*
     * Decorates an original ComparisonFailure, by being a ComparisonFailure
     * as well this just works with existing IDEs features such as those in
     * IntelliJ. IntelliJ provides diff like viewer when test are failed
     * due to comparison failures, it's quite useful.
     */

    private final String fullMessage;

    DecoratedComparisonFailure(ComparisonFailure src) {
        super(null, src.getExpected(), src.getActual());
        this.fullMessage = src.toString();
        initCause(src.getCause());
        setStackTrace(src.getStackTrace());
        for (Throwable sp : src.getSuppressed()) {
            addSuppressed(sp);
        }
    }

    @Override
    public String getMessage() {
        return fullMessage;
    }

    @Override
    public void printStackTrace(PrintStream s) {
        new Decorator(this).printSafely(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        new Decorator(this).printSafely(s);
    }

    @Override
    public String toString() {
        return "decorated " + getMessage();
    }
}
