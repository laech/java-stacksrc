package stack.source.junit4;

import org.junit.ComparisonFailure;
import org.junit.Test;
import stack.source.internal.Decorator;

import java.io.PrintStream;
import java.io.PrintWriter;

import static java.util.Collections.singleton;

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
        Decorator.printSafely(src, s, singleton(Test.class));
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        Decorator.printSafely(src, s, singleton(Test.class));
    }

}
