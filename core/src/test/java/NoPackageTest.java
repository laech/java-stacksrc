import static nz.lae.stacksrc.test.Assertions.assertStackTrace;

import nz.lae.stacksrc.DecoratedAssertionError;
import org.junit.jupiter.api.Test;

class NoPackageTest {

  @Test
  void noPackage() {
    try {
      throw new AssertionError("no package");
    } catch (AssertionError e) {
      var expected =
          """
java.lang.AssertionError: no package
	at NoPackageTest.noPackage(NoPackageTest.java:11)

	    9    void noPackage() {
	   10      try {
	-> 11        throw new AssertionError("no package");
	   12      } catch (AssertionError e) {
	   13        var expected =

""";
      assertStackTrace(expected, new DecoratedAssertionError(e));
    }
  }
}
