package stack.source.test;

import static org.junit.Assert.assertTrue;

public final class Lambda implements Runnable {

    @Override
    public void run() {
        String expected = "hi";
        lambda(() -> {
            assertTrue(true);
            throw new TestException(expected);
        });
    }

    private void lambda(Runnable code) {
        code.run();
    }

}
