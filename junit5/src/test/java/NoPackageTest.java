import static java.lang.Math.min;
import static java.lang.System.lineSeparator;
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
          String.join(
              lineSeparator(),
              "java.lang.AssertionError: no package",
              "\tat NoPackageTest.noPackage(NoPackageTest.java:16)",
              "",
              "\t   14    void noPackage() {",
              "\t   15      try {",
              "\t-> 16        throw new AssertionError(\"no package\");",
              "\t   17      } catch (AssertionError e) {",
              "\t   18        var expected =",
              "",
              "");
      var actual = StackTraceDecorator.print(e);
      actual = actual.substring(0, min(expected.length(), actual.length()));
      assertEquals(expected, actual);
    }
  }
}
