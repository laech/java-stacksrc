package nz.lae.stacksrc.junit4;

import static java.lang.Math.min;
import static nz.lae.stacksrc.core.Throwables.getStackTraceAsString;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

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
    assertArrayEquals("test message", new String[] {"1"}, new String[] {"2"});
  }

  @Rule
  public final RuleChain r =
      RuleChain.outerRule(ErrorDecoratorTest::apply).around(new ErrorDecorator());

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
      case "failure" -> assertFail(e);
      case "failByAssertEquals" -> assertFailByAssertEquals(e);
      case "failByAssertArrayEquals" -> assertFailByAssertArrayEquals(e);
      default -> throw new AssertionError(
          "Unexpected test method name: " + desc.getMethodName(), e);
    }
  }

  private static void assertFail(Throwable e) {
    var expected =
        """
nz.lae.stacksrc.junit4.DecoratedAssertionError:
java.lang.AssertionError: testing failure
	at org.junit.Assert.fail(Assert.java:89)
	at nz.lae.stacksrc.junit4.ErrorDecoratorTest.failure(ErrorDecoratorTest.java:19)

	   17    @Test
	   18    public void failure() {
	-> 19      fail("testing failure");
	   20    }

""";
    assertStackTrace(expected, e);
  }

  private static void assertFailByAssertEquals(Throwable e) {
    var expected =
        """
nz.lae.stacksrc.junit4.DecoratedAssertionError:
org.junit.ComparisonFailure: test message expected:<[1]> but was:<[2]>
	at org.junit.Assert.assertEquals(Assert.java:117)
	at nz.lae.stacksrc.junit4.ErrorDecoratorTest.failByAssertEquals(ErrorDecoratorTest.java:24)

	   22    @Test
	   23    public void failByAssertEquals() {
	-> 24      assertEquals("test message", "1", "2");
	   25    }

""";
    assertStackTrace(expected, e);
  }

  private static void assertFailByAssertArrayEquals(Throwable e) {
    var expected =
        """
nz.lae.stacksrc.junit4.DecoratedAssertionError:
test message: arrays first differed at element [0]; expected:<[1]> but was:<[2]>
	at org.junit.internal.ComparisonCriteria.arrayEquals(ComparisonCriteria.java:78)
	at org.junit.internal.ComparisonCriteria.arrayEquals(ComparisonCriteria.java:28)
	at org.junit.Assert.internalArrayEquals(Assert.java:534)
	at org.junit.Assert.assertArrayEquals(Assert.java:285)
	at nz.lae.stacksrc.junit4.ErrorDecoratorTest.failByAssertArrayEquals(ErrorDecoratorTest.java:29)

	   27    @Test
	   28    public void failByAssertArrayEquals() {
	-> 29      assertArrayEquals("test message", new String[] {"1"}, new String[] {"2"});
	   30    }

""";
    assertStackTrace(expected, e);
  }

  private static void assertStackTrace(String expected, Throwable e) {
    var actual = getStackTraceAsString(e);
    actual = actual.substring(0, min(expected.length(), actual.length()));
    assertEquals(expected, actual);
  }
}
