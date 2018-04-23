package stack.source.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import stack.source.internal.Decorator;

import java.util.Collection;

import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public final class StackSourceTest {

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return asList(new Object[][]{

                {new MultilineThrow(), new String[]{
                        "stack.source.test.TestException: hello world",
                        "\tat stack.source.test.MultilineThrow.run(MultilineThrow.java:7)",
                        "",
                        "\t-> 7          throw new TestException(",
                        "\t   8                  \"hello world\"",
                        "\t   9          );",
                        "",
                }},

                {new Chained(), new String[]{
                        "stack.source.test.TestException: what?",
                        "\tat stack.source.test.Chained.fail(Chained.java:15)",
                        "",
                        "\t-> 15          throw new TestException(message);",
                        "",
                        "\tat stack.source.test.Chained.run(Chained.java:10)",
                        "",
                        "\t    7          new Chained()",
                        "\t    8                  .nothing1(\"blah\")",
                        "\t    9                  .nothing2(\"meh\")",
                        "\t-> 10                  .fail(\"what?\")",
                        "",
                }},

                {new ThrowExceptionCreatedElseWhere(), new String[]{
                        "stack.source.test.TestException: testing",
                        "\tat stack.source.test.ThrowExceptionCreatedElseWhere.run(ThrowExceptionCreatedElseWhere.java:8)",
                        "",
                        "\t-> 8          TestException test = new TestException(\"testing\");",
                        "",
                }},

                {new ThrowNewException(), new String[]{
                        "stack.source.test.TestException: testing",
                        "\tat stack.source.test.ThrowNewException.run(ThrowNewException.java:7)",
                        "",
                        "\t-> 7          throw new TestException(\"testing\");",
                        "",
                }},

                {new ReturnFailure(), new String[]{
                        "stack.source.test.TestException: testing",
                        "\tat stack.source.test.ReturnFailure.bye(ReturnFailure.java:15)",
                        "",
                        "\t-> 15          throw new TestException(\"testing\");",
                        "",
                        "\tat stack.source.test.ReturnFailure.hi(ReturnFailure.java:11)",
                        "",
                        "\t-> 11          return bye();",
                        "",
                        "\tat stack.source.test.ReturnFailure.run(ReturnFailure.java:7)",
                        "",
                        "\t-> 7          System.err.println(hi());",
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
    public void test() {
        try {
            test.run();
        } catch (TestException e) {
            String actual = print(e).substring(0, expectedStackTraceBeginning.length());
            assertEquals(expectedStackTraceBeginning, actual);
        }
    }

    private static String print(Throwable e) {
        return new Decorator(e).print();
    }

}
