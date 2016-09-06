package stack.source.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.util.Collection;

import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static stack.source.internal.Throwables.printStackTraceWithSource;

@RunWith(Parameterized.class)
public final class StackSourceTest {

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return asList(new Object[][]{

                {new Chained(), new String[]{
                        "stack.source.test.TestException: what?",
                        "\tat stack.source.test.Chained.fail(Chained.java:15)",
                        "",
                        "\t\t-> 15          throw new TestException(message);",
                        "",
                        "\tat stack.source.test.Chained.run(Chained.java:10)",
                        "",
                        "\t\t    7          new Chained()",
                        "\t\t    8                  .nothing1(\"blah\")",
                        "\t\t    9                  .nothing2(\"meh\")",
                        "\t\t-> 10                  .fail(\"what?\")",
                        "",
                }},

                {new ThrowExceptionCreatedElseWhere(), new String[]{
                        "stack.source.test.TestException: testing",
                        "\tat stack.source.test.ThrowExceptionCreatedElseWhere.run(ThrowExceptionCreatedElseWhere.java:8)",
                        "",
                        "\t\t-> 8          TestException test = new TestException(\"testing\");",
                        "",
                }},

                {new ThrowNewException(), new String[]{
                        "stack.source.test.TestException: testing",
                        "\tat stack.source.test.ThrowNewException.run(ThrowNewException.java:7)",
                        "",
                        "\t\t-> 7          throw new TestException(\"testing\");",
                        "",
                }},

                {new ReturnFailure(), new String[]{
                        "stack.source.test.TestException: testing",
                        "\tat stack.source.test.ReturnFailure.bye(ReturnFailure.java:15)",
                        "",
                        "\t\t-> 15          throw new TestException(\"testing\");",
                        "",
                        "\tat stack.source.test.ReturnFailure.hi(ReturnFailure.java:11)",
                        "",
                        "\t\t-> 11          return bye();",
                        "",
                        "\tat stack.source.test.ReturnFailure.run(ReturnFailure.java:7)",
                        "",
                        "\t\t-> 7          System.err.println(hi());",
                        "",
                }},
        });
    }

    private final Runnable test;
    private final String expectedStackTraceBeginning;

    public StackSourceTest(Runnable test, CharSequence[] expectedStackTraceBeginningLines) {
        this.test = test;
        this.expectedStackTraceBeginning = String.join(lineSeparator(), expectedStackTraceBeginningLines);
    }

    @Test
    public void test() throws Exception {
        try {
            test.run();
        } catch (TestException e) {
            String actual = print(e).substring(0, expectedStackTraceBeginning.length());
            assertEquals(expectedStackTraceBeginning, actual);
        }
    }

    private static String print(Throwable e) throws IOException {
        StringBuilder builder = new StringBuilder();
        printStackTraceWithSource(e, builder);
        return builder.toString();
    }

}
