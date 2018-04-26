import org.junit.Test;
import stack.source.internal.Decorator;
import stack.source.test.TestException;

import static java.lang.Math.min;
import static java.lang.System.lineSeparator;
import static org.junit.Assert.assertEquals;

public final class NoPackageTest {

    @Test
    public void noPackage() throws Exception {
        try {
            new NoPackage().run();
        } catch (TestException e) {
            String expected = String.join(lineSeparator(),
                    "stack.source.test.TestException: no package",
                    "\tat NoPackage.run(NoPackage.java:7)",
                    "",
                    "\t   5      @Override",
                    "\t   6      public void run() {",
                    "\t-> 7          throw new TestException(\"no package\");",
                    "\t   8      }",
                    "",
                    ""
            );
            String actual = new Decorator(e).print();
            actual = actual.substring(0, min(expected.length(), actual.length()));
            assertEquals(expected, actual);
        }
    }
}
