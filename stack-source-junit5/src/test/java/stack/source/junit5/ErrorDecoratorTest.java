package stack.source.junit5;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import stack.source.internal.DecoratedError;

import static java.lang.Math.min;
import static java.lang.System.getProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith({
        ErrorDecoratorTest.AssertDecoration.class,
        ErrorDecorator.class
})
class ErrorDecoratorTest {

    @Test
    void decoratesFailure() {
        new Fail().run();
    }

    @Test
    void assumeApiPassThrough() {
        assumeTrue(false);
    }

    @Test
    @Disabled
    void disabledApiPassThrough() {
    }

    static class AssertDecoration implements TestExecutionExceptionHandler {

        @Override
        public void handleTestExecutionException(
                ExtensionContext context,
                Throwable throwable
        ) throws Throwable {

            if (!context.getRequiredTestMethod().getName().equals("decoratesFailure")) {
                throw throwable;
            }

            String expected = String.join(getProperty("line.separator"),
                    "org.opentest4j.AssertionFailedError: testing failure",
                    "\tat org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:36)",
                    "\tat org.junit.jupiter.api.Assertions.fail(Assertions.java:62)",
                    "\tat stack.source.junit5.Fail.run(Fail.java:8)",
                    "",
                    "\t-> 8          fail(\"testing failure\");",
                    ""
            );
            String actual = throwable.getMessage();
            actual = actual.substring(0, min(expected.length(), actual.length()));
            assertEquals(DecoratedError.class, throwable.getClass());
            assertEquals(expected, actual);
        }
    }
}
