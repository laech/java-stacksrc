package stack.source.test;

import org.junit.Test;
import stack.source.internal.Decorator;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;

public abstract class StackSourceTestBase {

    private final Runnable test;
    private final String expectedStackTraceBeginning;
    private final Function<String, String> processor;

    StackSourceTestBase(
            Runnable test,
            String expectedStackTraceBeginning,
            Function<String, String> processor) {
        this.test = requireNonNull(test);
        this.expectedStackTraceBeginning = requireNonNull(expectedStackTraceBeginning);
        this.processor = processor;
    }

    @Test
    public void test() {
        try {
            test.run();
        } catch (TestException e) {

            String actual = Decorator.print(e);
            if (processor != null) {
                actual = processor.apply(actual);
            }
            actual = actual.substring(0, expectedStackTraceBeginning.length());
            assertEquals(expectedStackTraceBeginning, actual);
        }
    }

}
