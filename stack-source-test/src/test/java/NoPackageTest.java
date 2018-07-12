import org.junit.Test;
import stack.source.internal.Decorator;
import stack.source.test.TestException;

import static java.lang.Math.min;
import static java.lang.System.lineSeparator;
import static org.junit.Assert.assertEquals;

public final class NoPackageTest {

    @Test
    public void noPackage() {
        try {
            new NoPackage().run();
        } catch (TestException e) {
            String expected = String.join(lineSeparator(),
                    "stack.source.test.TestException: no package",
                    "\tat NoPackage.run(NoPackage.java:7)",
                    "\tat NoPackageTest.noPackage(NoPackageTest.java:14)",
                    "",
                    "\t-> 14              new NoPackage().run();",
                    "",
                    ""
            );
            String actual = Decorator.print(e);
            actual = actual.substring(0, min(expected.length(), actual.length()));
            assertEquals(expected, actual);
        }
    }
}
