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
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;
import static stack.source.internal.Throwables.getStackTraceAsString;

public final class ErrorDecoratorTest {

  @Test
  public void failure() {
    fail("testing failure");
  }

  @Test
  public void failByAssertEquals() {
    assertEquals("test message", "1", "2");
  }

  @Test
  public void failByAssertArrayEquals() {
    assertArrayEquals(
      "test message",
      new String[]{"1"},
      new String[]{"2"}
    );
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  public void assumeApiPassThrough() {
    assumeThat(false, is(true));
  }

  @Test
  @Ignore
  public void ignoreApiPassThrough() {
  }

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
        } catch (DecoratedAssertionError e) {
          assertFailure(e, description);
        }
      }
    };
  }

  private static void assertFailure(Throwable e, Description desc) {
    switch (desc.getMethodName()) {
      case "failure":
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
    String expected = String.join(
      lineSeparator(),
      "java.lang.AssertionError: testing failure",
      "\tat org.junit.Assert.fail(Assert.java:88)",
      "\tat stack.source.junit4.ErrorDecoratorTest.failure(ErrorDecoratorTest.java:21)",
      "",
      "\t   19    @Test",
      "\t   20    public void failure() {",
      "\t-> 21      fail(\"testing failure\");",
      "\t   22    }",
      ""
    );
    assertStackTrace(expected, e);
  }

  private static void assertFailByAssertEquals(Throwable e) {
    String expected = String.join(
      lineSeparator(),
      "org.junit.ComparisonFailure: test message expected:<[1]> but was:<[2]>",
      "\tat org.junit.Assert.assertEquals(Assert.java:115)",
      "\tat stack.source.junit4.ErrorDecoratorTest.failByAssertEquals(ErrorDecoratorTest.java:26)",
      "",
      "\t   24    @Test",
      "\t   25    public void failByAssertEquals() {",
      "\t-> 26      assertEquals(\"test message\", \"1\", \"2\");",
      "\t   27    }",
      ""
    );
    assertStackTrace(expected, e);
  }

  private static void assertFailByAssertArrayEquals(Throwable e) {
    String expected = String.join(
      lineSeparator(),
      "test message: arrays first differed at element [0]; expected:<[1]> but was:<[2]>",
      "\tat org.junit.internal.ComparisonCriteria.arrayEquals(ComparisonCriteria.java:55)",
      "\tat org.junit.Assert.internalArrayEquals(Assert.java:532)",
      "\tat org.junit.Assert.assertArrayEquals(Assert.java:283)",
      "\tat stack.source.junit4.ErrorDecoratorTest.failByAssertArrayEquals(ErrorDecoratorTest.java:31)",
      "",
      "\t-> 31      assertArrayEquals(",
      "\t   32        \"test message\",",
      "\t   33        new String[]{\"1\"},",
      "\t   34        new String[]{\"2\"}",
      "\t   35      );",
      ""
    );
    assertStackTrace(expected, e);
  }

  private static void assertStackTrace(String expected, Throwable e) {
    String actual = getStackTraceAsString(e);
    actual = actual.substring(0, min(expected.length(), actual.length()));
    assertEquals(expected, actual);
  }
}
