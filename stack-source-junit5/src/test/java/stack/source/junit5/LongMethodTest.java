package stack.source.junit5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import static java.lang.Math.min;
import static java.lang.String.join;
import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static stack.source.internal.Throwables.getStackTraceAsString;

@ExtendWith({
        LongMethodTest.AssertDecoration.class,
        ErrorDecorator.class
})
class LongMethodTest {

    @Test
    void test() {
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        assertEquals(1, 1);
        throw new AssertionError("test");
    }


    static class AssertDecoration implements TestExecutionExceptionHandler {

        @Override
        public void handleTestExecutionException(ExtensionContext context, Throwable e) {
            String expected = join(lineSeparator(),
                    "java.lang.AssertionError: test",
                    "\tat stack.source.junit5.LongMethodTest.test(LongMethodTest.java:48)",
                    "",
                    "\t-> 48          throw new AssertionError(\"test\");",
                    ""
            );
            assertEquals(DecoratedAssertionFailedError.class, e.getClass());
            assertStackTrace(expected, e);
        }
    }

    private static void assertStackTrace(String expected, Throwable e) {
        String actual = getStackTraceAsString(e);
        actual = actual.substring(0, min(expected.length(), actual.length()));
        assertEquals(expected, actual);
    }
}
