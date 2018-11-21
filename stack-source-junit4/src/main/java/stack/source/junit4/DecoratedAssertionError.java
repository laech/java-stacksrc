package stack.source.junit4;

import org.junit.Test;
import stack.source.internal.Decorator;

import java.io.PrintStream;
import java.io.PrintWriter;

import static java.util.Collections.singleton;

final class DecoratedAssertionError extends AssertionError {

    private final Throwable src;

    DecoratedAssertionError(Throwable src) {
        super(src.toString(), src.getCause());
        setStackTrace(src.getStackTrace());
        for (Throwable sp : src.getSuppressed()) {
            addSuppressed(sp);
        }
        this.src = src;
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
