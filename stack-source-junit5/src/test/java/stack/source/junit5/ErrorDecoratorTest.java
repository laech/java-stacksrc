package stack.source.junit5;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import static java.lang.Math.min;
import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static stack.source.internal.Throwables.getStackTraceAsString;

@ExtendWith({
        ErrorDecoratorTest.AssertDecoration.class,
        ErrorDecorator.class
})
class ErrorDecoratorTest {

    @Test
    void decoratesFailure() {
        fail("testing failure");
    }

    @Test
    void decoratesArrayFailure() {
        assertArrayEquals(new int[]{1}, new int[]{2});
    }

    @Test
    void assumeApiPassThrough() {
        assumeTrue(() -> false);
    }

    @Test
    @Disabled
    void disabledApiPassThrough() {
    }

    static class AssertDecoration implements TestExecutionExceptionHandler {

        @Override
        public void handleTestExecutionException(
                ExtensionContext context,
                Throwable e
        ) throws Throwable {

            switch (context.getRequiredTestMethod().getName()) {
                case "decoratesFailure":
                    assertEqualsFailure(e);
                    break;
                case "decoratesArrayFailure":
                    assertArrayEqualsFailure(e);
                    break;
                default:
                    throw e;
            }
        }

        private void assertEqualsFailure(Throwable e) {
            String expected = String.join(lineSeparator(),
                    "org.opentest4j.AssertionFailedError: testing failure",
                    "\tat org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:36)",
                    "\tat org.junit.jupiter.api.Assertions.fail(Assertions.java:62)",
                    "\tat stack.source.junit5.ErrorDecoratorTest.decoratesFailure(ErrorDecoratorTest.java:23)",
                    "",
                    "\t   21      @Test",
                    "\t   22      void decoratesFailure() {",
                    "\t-> 23          fail(\"testing failure\");",
                    "\t   24      }",
                    ""
            );
            assertEquals(DecoratedAssertionFailedError.class, e.getClass());
            assertStackTrace(expected, e);
        }

        private void assertArrayEqualsFailure(Throwable e) {
            String expected = String.join(lineSeparator(),
                    "org.opentest4j.AssertionFailedError: array contents differ at index [0], expected: <1> but was: <2>",
                    "\tat org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:36)",
                    "\tat org.junit.jupiter.api.AssertArrayEquals.failArraysNotEqual(AssertArrayEquals.java:434)",
                    "\tat org.junit.jupiter.api.AssertArrayEquals.assertArrayEquals(AssertArrayEquals.java:246)",
                    "\tat org.junit.jupiter.api.AssertArrayEquals.assertArrayEquals(AssertArrayEquals.java:92)",
                    "\tat org.junit.jupiter.api.AssertArrayEquals.assertArrayEquals(AssertArrayEquals.java:88)",
                    "\tat org.junit.jupiter.api.Assertions.assertArrayEquals(Assertions.java:622)",
                    "\tat stack.source.junit5.ErrorDecoratorTest.decoratesArrayFailure(ErrorDecoratorTest.java:28)",
                    "",
                    "\t   26      @Test",
                    "\t   27      void decoratesArrayFailure() {",
                    "\t-> 28          assertArrayEquals(new int[]{1}, new int[]{2});",
                    "\t   29      }",
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
