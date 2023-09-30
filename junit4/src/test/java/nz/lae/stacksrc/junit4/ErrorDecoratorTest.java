package nz.lae.stacksrc.junit4;

import static java.lang.Math.min;
import static nz.lae.stacksrc.core.Throwables.getStackTraceAsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runners.model.Statement;

public final class ErrorDecoratorTest {

  @Test
  public void decoratesFailure() {
    fail("testing failure");
  }

  @Rule
  public final RuleChain r =
      RuleChain.outerRule((base, __) -> apply(base)).around(new ErrorDecorator());

  private static Statement apply(Statement base) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          base.evaluate();
        } catch (DecoratedAssertionError e) {
          assertFailure(e);
        }
      }
    };
  }

  private static void assertFailure(Throwable e) {
    var expected =
        """
nz.lae.stacksrc.junit4.DecoratedAssertionError:
java.lang.AssertionError: testing failure
	at org.junit.Assert.fail(Assert.java:89)
	at nz.lae.stacksrc.junit4.ErrorDecoratorTest.decoratesFailure(ErrorDecoratorTest.java:17)

	   15    @Test
	   16    public void decoratesFailure() {
	-> 17      fail("testing failure");
	   18    }

""";
    assertStackTrace(expected, e);
  }

  private static void assertStackTrace(String expected, Throwable e) {
    var actual = getStackTraceAsString(e);
    actual = actual.substring(0, min(expected.length(), actual.length()));
    assertEquals(expected, actual);
  }
}
