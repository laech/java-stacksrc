import static java.lang.Math.min;
import static org.junit.jupiter.api.Assertions.assertEquals;

import nz.lae.stacksrc.core.StackTraceDecorator;
import nz.lae.stacksrc.junit5.ErrorDecorator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({ErrorDecorator.class})
final class NoPackageTest {

  @Test
  void noPackage() {
    try {
      throw new AssertionError("no package");
    } catch (AssertionError e) {
      var expected =
          """
java.lang.AssertionError: no package
	at NoPackageTest.noPackage(NoPackageTest.java:15)

	   13    void noPackage() {
	   14      try {
	-> 15        throw new AssertionError("no package");
	   16      } catch (AssertionError e) {
	   17        var expected =

""";
      var actual = StackTraceDecorator.print(e);
      actual = actual.substring(0, min(expected.length(), actual.length()));
      assertEquals(expected, actual);
    }
  }
}
