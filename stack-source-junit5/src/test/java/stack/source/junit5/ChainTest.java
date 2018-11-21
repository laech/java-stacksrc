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
        ChainTest.AssertDecoration.class,
        ErrorDecorator.class
})
class ChainTest {

    @Test
    void test() {
        new ChainTest()
                .nothing1()
                .nothing2()
                .fail("what?")
                .nothing3()
                .fail("more?")
                .fail("and more?")
                .fail("and more more?");
    }

    private ChainTest fail(String message) {
        throw new AssertionError(message);
    }

    private ChainTest nothing1() {
        return this;
    }

    private ChainTest nothing2() {
        return this;
    }

    private ChainTest nothing3() {
        return this;
    }

    static class AssertDecoration implements TestExecutionExceptionHandler {

        @Override
        public void handleTestExecutionException(ExtensionContext context, Throwable e) {
            String expected = join(lineSeparator(),
                    "java.lang.AssertionError: what?",
                    "\tat stack.source.junit5.ChainTest.fail(ChainTest.java:33)",
                    "",
                    "\t   32      private ChainTest fail(String message) {",
                    "\t-> 33          throw new AssertionError(message);",
                    "\t   34      }",
                    "",
                    "",
                    "\tat stack.source.junit5.ChainTest.test(ChainTest.java:25)",
                    "",
                    "\t   22          new ChainTest()",
                    "\t   23                  .nothing1()",
                    "\t   24                  .nothing2()",
                    "\t-> 25                  .fail(\"what?\")",
                    "\t   26                  .nothing3()",
                    "\t   27                  .fail(\"more?\")",
                    "\t   28                  .fail(\"and more?\")",
                    "\t   29                  .fail(\"and more more?\");",
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
