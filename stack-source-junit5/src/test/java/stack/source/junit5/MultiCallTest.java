package stack.source.junit5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import java.util.List;
import java.util.Optional;

import static java.lang.Math.min;
import static java.lang.String.join;
import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static stack.source.internal.Throwables.getStackTraceAsString;

@ExtendWith({
        MultiCallTest.AssertDecoration.class,
        ErrorDecorator.class
})
class MultiCallTest {

    @Test
    void test() {
        assertList(asList("bob", "bob"));
        assertList(asList("abc", "def"));
    }


    private void assertString(String s) {
        if (s.equals("bob")) {
            throw new AssertionError("bob");
        }
    }

    private void assertList(List<String> list) {
        list.forEach(s -> Optional.of(s.toLowerCase()).ifPresent(ss -> {
            if (ss.length() > 1000) {
                throw new AssertionError("no");
            }
            assertString(ss);
        }));
    }

    static class AssertDecoration implements TestExecutionExceptionHandler {

        @Override
        public void handleTestExecutionException(ExtensionContext context, Throwable e) {
            String expected = join(lineSeparator(),
                    "java.lang.AssertionError: bob",
                    "\tat stack.source.junit5.MultiCallTest.assertString(MultiCallTest.java:33)",
                    "",
                    "\t   32          if (s.equals(\"bob\")) {",
                    "\t-> 33              throw new AssertionError(\"bob\");",
                    "\t   34          }",
                    "",
                    "",
                    "\tat stack.source.junit5.MultiCallTest.lambda$null$0(MultiCallTest.java:42)",
                    "",
                    "\t   38          list.forEach(s -> Optional.of(s.toLowerCase()).ifPresent(ss -> {",
                    "\t   39              if (ss.length() > 1000) {",
                    "\t   40                  throw new AssertionError(\"no\");",
                    "\t   41              }",
                    "\t-> 42              assertString(ss);",
                    "\t   43          }));",
                    "",
                    "",
                    "\tat java.util.Optional.ifPresent(xxx)",
                    "\tat stack.source.junit5.MultiCallTest.lambda$assertList$1(MultiCallTest.java:38)",
                    "\tat java.util.Arrays$ArrayList.forEach(xxx)",
                    "\tat stack.source.junit5.MultiCallTest.assertList(MultiCallTest.java:38)",
                    "\tat stack.source.junit5.MultiCallTest.test(MultiCallTest.java:26)",
                    "",
                    "\t   24      @Test",
                    "\t   25      void test() {",
                    "\t-> 26          assertList(asList(\"bob\", \"bob\"));",
                    "\t   27          assertList(asList(\"abc\", \"def\"));",
                    "\t   28      }",
                    ""
            );
            assertEquals(DecoratedAssertionFailedError.class, e.getClass());
            assertStackTrace(expected, e);
        }
    }

    private static void assertStackTrace(String expected, Throwable e) {
        // Temporary hack to make this work on Java 9 too
        String actual = getStackTraceAsString(e)
                .replaceAll("java.base/", "")
                .replaceAll("Optional\\.java:\\d+", "xxx")
                .replaceAll("Arrays\\.java:\\d+", "xxx")
                .replace("lambda$assertList$0", "lambda$null$0");
        actual = actual.substring(0, min(expected.length(), actual.length()));
        assertEquals(expected, actual);
    }
}
