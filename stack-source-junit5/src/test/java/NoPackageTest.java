
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import stack.source.internal.Decorator;
import stack.source.junit5.ErrorDecorator;

import static java.lang.Math.min;
import static java.lang.System.lineSeparator;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({ErrorDecorator.class})
final class NoPackageTest {

    @Test
    void noPackage() {
        try {
            throw new AssertionError("no package");
        } catch (AssertionError e) {
            String expected = String.join(lineSeparator(),
                    "java.lang.AssertionError: no package",
                    "\tat NoPackageTest.noPackage(NoPackageTest.java:18)",
                    "",
                    "\t-> 18              throw new AssertionError(\"no package\");",
                    ""
            );
            String actual = Decorator.print(e, singleton(Test.class));
            actual = actual.substring(0, min(expected.length(), actual.length()));
            assertEquals(expected, actual);
        }
    }
}
