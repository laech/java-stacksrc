package stack.source.junit4;

import static org.junit.Assert.assertEquals;

final class FailByAssertEquals implements Runnable {
    @Override
    public void run() {
        assertEquals("test message", "1", "2");
    }
}
