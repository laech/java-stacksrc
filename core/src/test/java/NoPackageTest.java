import static nz.lae.stacksrc.core.Assertions.assertStackTrace;

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
	at NoPackageTest.noPackage(NoPackageTest.java:10)

	    8    void noPackage() {
	    9      try {
	-> 10        throw new AssertionError("no package");
	   11      } catch (AssertionError e) {
	   12        var expected =

""";
      assertStackTrace(expected, e);
    }
  }
}
