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
        MultiLongChainsTest.AssertDecoration.class,
        ErrorDecorator.class
})
class MultiLongChainsTest {

    @Test
    void test() {

        Helper helper1 = new Helper();
        Helper helper2 = helper1
                .test("x")
                .test("x")
                .test("x");

        test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x");

        test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x");
    }


    static class AssertDecoration implements TestExecutionExceptionHandler {

        @Override
        public void handleTestExecutionException(ExtensionContext context, Throwable e) {
            String expected = join(lineSeparator(),
                    "java.lang.AssertionError: bob",
                    "\tat stack.source.junit5.MultiLongChainsTest$Helper.test(MultiLongChainsTest.java:93)",
                    "\tat stack.source.junit5.MultiLongChainsTest$Helper.access$100(MultiLongChainsTest.java:91)",
                    "\tat stack.source.junit5.MultiLongChainsTest.test(MultiLongChainsTest.java:25)",
                    "",
                    "\t   24          Helper helper2 = helper1",
                    "\t-> 25                  .test(\"x\")",
                    "\t   26                  .test(\"x\")",
                    "\t   27                  .test(\"x\");"
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

    private MultiLongChainsTest test(String msg) {
        return this;
    }

    private static class Helper {
        private Helper test(String msg) {
            throw new AssertionError("bob");
        }
    }
}
