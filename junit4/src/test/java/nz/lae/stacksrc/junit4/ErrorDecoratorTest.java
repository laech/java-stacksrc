package nz.lae.stacksrc.junit4;

import static nz.lae.stacksrc.test.Assertions.assertStackTraceHasExpectedPrefix;
import static org.junit.Assert.fail;

import nz.lae.stacksrc.DecoratedAssertionError;
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

  private static void assertFailure(DecoratedAssertionError e) {
    var expected =
        """
java.lang.AssertionError: testing failure
	at org.junit.Assert.fail(Assert.java:89)
	at nz.lae.stacksrc.junit4.ErrorDecoratorTest.decoratesFailure(ErrorDecoratorTest.java:16)

	   14    @Test
	   15    public void decoratesFailure() {
	-> 16      fail("testing failure");
	   17    }

""";
    assertStackTraceHasExpectedPrefix(expected, e);
  }
}
