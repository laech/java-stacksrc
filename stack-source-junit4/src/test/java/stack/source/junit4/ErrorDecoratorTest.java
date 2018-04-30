package stack.source.junit4;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static java.lang.Math.min;
import static java.lang.System.lineSeparator;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;
import static stack.source.internal.Throwables.getStackTraceAsString;

public final class ErrorDecoratorTest {

    @Rule
    public final RuleChain r = RuleChain
            .outerRule(ErrorDecoratorTest::apply)
            .around(new ErrorDecorator());

    private static Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (DecoratedAssertionError | DecoratedComparisonFailure e) {
                    assertFailure(e, description);
                }
            }
        };
    }

    private static void assertFailure(Throwable e, Description desc) {
        switch (desc.getMethodName()) {
            case "fail":
                assertFail(e);
                break;
            case "failByAssertEquals":
                assertFailByAssertEquals(e);
                break;
            case "failByAssertArrayEquals":
                assertFailByAssertArrayEquals(e);
                break;
            default:
                throw new AssertionError("Unexpected test method name: "
                        + desc.getMethodName(), e);
        }
    }

    private static void assertFail(Throwable e) {
        String expected = String.join(lineSeparator(),
                "java.lang.AssertionError: testing failure",
                "\tat org.junit.Assert.fail(Assert.java:88)",
                "\tat stack.source.junit4.Fail.run(Fail.java:8)",
                "",
                "\t   6      @Override",
                "\t   7      public void run() {",
                "\t-> 8          fail(\"testing failure\");",
                "\t   9      }",
                "",
                ""
        );
        assertStackTrace(expected, e);
    }

    private static void assertFailByAssertEquals(Throwable e) {
        String expected = String.join(lineSeparator(),
                "org.junit.ComparisonFailure: test message expected:<[1]> but was:<[2]>",
                "\tat org.junit.Assert.assertEquals(Assert.java:115)",
                "\tat stack.source.junit4.FailByAssertEquals.run(FailByAssertEquals.java:8)",
                "",
                "\t   6      @Override",
                "\t   7      public void run() {",
                "\t-> 8          assertEquals(\"test message\", \"1\", \"2\");",
                "\t   9      }",
                "",
                ""
        );
        assertStackTrace(expected, e);
    }

    private static void assertFailByAssertArrayEquals(Throwable e) {
        String expected = String.join(lineSeparator(),
                "test message: arrays first differed at element [0]; expected:<[1]> but was:<[2]>",
                "\tat org.junit.internal.ComparisonCriteria.arrayEquals(ComparisonCriteria.java:55)",
                "\tat org.junit.Assert.internalArrayEquals(Assert.java:532)",
                "\tat org.junit.Assert.assertArrayEquals(Assert.java:283)",
                "\tat stack.source.junit4.FailByAssertArrayEquals.run(FailByAssertArrayEquals.java:8)",
                "",
                "\t->  8          assertArrayEquals(",
                "\t    9                  \"test message\",",
                "\t   10                  new String[]{\"1\"},",
                "\t   11                  new String[]{\"2\"});",
                "",
                ""
        );
        assertStackTrace(expected, e);
    }

    private static void assertStackTrace(String expected, Throwable e) {
        String actual = getStackTraceAsString(e);
        actual = actual.substring(0, min(expected.length(), actual.length()));
        assertEquals(expected, actual);
    }

    @Test
    public void fail() {
        new Fail().run();
    }

    @Test
    public void failByAssertEquals() {
        new FailByAssertEquals().run();
    }

    @Test
    public void failByAssertArrayEquals() {
        new FailByAssertArrayEquals().run();
    }

    @Test
    public void assumeApiPassThrough() {
        assumeThat(false, is(true));
    }

    @Test
    @Ignore
    public void ignoreApiPassThrough() {
    }
}
