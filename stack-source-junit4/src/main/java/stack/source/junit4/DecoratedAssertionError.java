package stack.source.junit4;

import stack.source.internal.Decorator;

import java.io.PrintStream;
import java.io.PrintWriter;

final class DecoratedAssertionError extends AssertionError {

    DecoratedAssertionError(Throwable src) {
        super(src.toString(), src.getCause());
        setStackTrace(src.getStackTrace());
        for (Throwable sp : src.getSuppressed()) {
            addSuppressed(sp);
        }
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
