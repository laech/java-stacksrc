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

    private final ComparisonFailure src;

    DecoratedComparisonFailure(ComparisonFailure src) {
        super(null, src.getExpected(), src.getActual());
        initCause(src.getCause());
        setStackTrace(src.getStackTrace());
        for (Throwable sp : src.getSuppressed()) {
            addSuppressed(sp);
        }
        this.src = src;
    }

    @Override
    public String getMessage() {
        return src.toString();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        new Decorator(src).printSafely(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        new Decorator(src).printSafely(s);
    }

}
