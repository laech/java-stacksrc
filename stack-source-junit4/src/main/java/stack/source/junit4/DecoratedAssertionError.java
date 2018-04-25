package stack.source.junit4;

import stack.source.internal.Decorator;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

final class DecoratedAssertionError extends AssertionError {

    DecoratedAssertionError(Throwable src) {
        super(src.getMessage(), src.getCause());
        setStackTrace(src.getStackTrace());
        for (Throwable sp : src.getSuppressed()) {
            addSuppressed(sp);
        }
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

}
