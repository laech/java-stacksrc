package stack.source.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import stack.source.internal.Decorator;

import java.io.IOException;
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
                        "",
                        "\tat stack.source.test.ReturnFailure.hi(ReturnFailure.java:11)",
                        "\tat stack.source.test.ReturnFailure.run(ReturnFailure.java:7)",
                        "",
                        "\t-> 7          System.err.println(hi());",
                        "",
                }},
                {new MultiCalls(), new String[]{
                        "stack.source.test.TestException: bob",
                        "\tat stack.source.test.MultiCalls.assertString(MultiCalls.java:12)",
                        "",
                        "\t-> 12              throw new TestException(\"bob\");",
                        "",
                        "",
                        "\tat stack.source.test.MultiCalls.lambda$null$0(MultiCalls.java:21)",
                        "",
                        "\t   17          list.forEach(s -> Optional.of(s.toLowerCase()).ifPresent(ss -> {",
                        "\t   18              if (ss.length() > 1000) {",
                        "\t   19                  throw new TestException(\"no\");",
                        "\t   20              }",
                        "\t-> 21              assertString(ss);",
                        "\t   22          }));",
                        "",
                        "",
                        "\tat java.util.Optional.ifPresent(Optional.java:159)",
                        "\tat stack.source.test.MultiCalls.lambda$assertList$1(MultiCalls.java:17)",
                        "\tat java.util.Arrays$ArrayList.forEach(Arrays.java:3880)",
                        "\tat stack.source.test.MultiCalls.assertList(MultiCalls.java:17)",
                        "\tat stack.source.test.MultiCalls.run(MultiCalls.java:27)",
                        "",
                        "\t-> 27          assertList(asList(\"bob\", \"bob\"));",
                        "",
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
        return new Decorator(e).print();
    }

}
