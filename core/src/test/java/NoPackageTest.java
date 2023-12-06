import static nz.lae.stacksrc.test.Assertions.assertStackTraceHasExpectedPrefix;

import nz.lae.stacksrc.DecoratedAssertionError;
import org.junit.jupiter.api.Test;

@SuppressWarnings("DefaultPackage")
class NoPackageTest {

  @Test
  void noPackage() {
    try {
      throw new AssertionError("no package");
    } catch (AssertionError e) {
      var expected =
          """
java.lang.AssertionError: no package
	at NoPackageTest.noPackage(NoPackageTest.java:12)

	   10    void noPackage() {
	   11      try {
	-> 12        throw new AssertionError("no package");
	   13      } catch (AssertionError e) {
	   14        var expected =

""";
      assertStackTraceHasExpectedPrefix(expected, new DecoratedAssertionError(e));
    }
  }
}
