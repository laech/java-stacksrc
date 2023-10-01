package nz.lae.stacksrc.junit4;

import static nz.lae.stacksrc.test.Assertions.assertStackTrace;
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
	at nz.lae.stacksrc.junit4.ErrorDecoratorTest.decoratesFailure(ErrorDecoratorTest.java:15)

	   13    @Test
	   14    public void decoratesFailure() {
	-> 15      fail("testing failure");
	   16    }

""";
    assertStackTrace(expected, e);
  }
}
