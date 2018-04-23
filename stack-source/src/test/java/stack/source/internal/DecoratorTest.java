package stack.source.internal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public final class DecoratorTest {

    @Parameters(name = "{0}")
    public static Collection<Throwable[]> data() {
        return asList(new Throwable[][]{
                {simpleThrowable()},
                {hasCircularThrowable()},
                {hasSuppressedThrowable()},
                {hasCauseThrowable()},
                {hasEmptyStackTrace()},
        });
    }

    private static Throwable simpleThrowable() {
        return new Exception("Simple");
    }

    private static Throwable hasCircularThrowable() {
        Throwable a = new Exception("Circular reference");
        Throwable b = new IndexOutOfBoundsException();
        a.initCause(b);
        b.initCause(a);
        return a;
    }

    private static Throwable hasSuppressedThrowable() {
        Throwable e = new Exception("Has suppressed");
        e.addSuppressed(new IllegalStateException());
        e.addSuppressed(new IllegalArgumentException());
        return e;
    }

    private static Throwable hasCauseThrowable() {
        return new Exception("Has cause", new RuntimeException());
    }

    private static Throwable hasEmptyStackTrace() {
        Exception e = new Exception("Has no stack");
        e.setStackTrace(new StackTraceElement[0]);
        return e;
    }

    private final Throwable e;

    public DecoratorTest(Throwable e) {
        this.e = e;
    }

    @Test
    public void formatsStackTraceSameAsJdk() throws Exception {
        assertEquals(getJdkStackTrace(), getOurStackTrace());
    }

    private String getJdkStackTrace() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }

    private String getOurStackTrace() throws IOException {
        return new Decorator(e, false).print();
    }
}
