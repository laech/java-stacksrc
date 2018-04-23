package stack.source.internal;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public final class DecoratedError extends AssertionError {

    private final Throwable origin;

    public DecoratedError(Throwable origin) {
        // TODO handle failure
        super(new Decorator(origin).print());
        this.origin = requireNonNull(origin);
        setStackTrace(new StackTraceElement[0]);
    }

    public Throwable getOrigin() {
        return origin;
    }

    @Override
    public String toString() {
        return format("%s:%n%s", getClass().getName(), getLocalizedMessage());
    }
}
