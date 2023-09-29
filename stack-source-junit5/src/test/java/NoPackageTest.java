
import static java.lang.Math.min;
import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import stack.source.internal.Decorator;
import stack.source.junit5.ErrorDecorator;

@ExtendWith({ErrorDecorator.class})
final class NoPackageTest {

    @Test
    void noPackage() {
        try {
            throw new AssertionError("no package");
        } catch (AssertionError e) {
          var expected = String.join(lineSeparator(),
                    "java.lang.AssertionError: no package",
                    "\tat NoPackageTest.noPackage(NoPackageTest.java:17)",
                    "",
                    "\t   15      void noPackage() {",
                    "\t   16          try {",
                    "\t-> 17              throw new AssertionError(\"no package\");",
                    "\t   18          } catch (AssertionError e) {",
                    "\t   19            var expected = String.join(lineSeparator(),",
                    "",
                    ""
            );
          var actual = Decorator.print(e);
            actual = actual.substring(0, min(expected.length(), actual.length()));
            assertEquals(expected, actual);
        }
    }
}
