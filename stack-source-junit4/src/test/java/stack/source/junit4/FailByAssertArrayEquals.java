package stack.source.junit4;

import static org.junit.Assert.assertArrayEquals;

final class FailByAssertArrayEquals implements Runnable {
    @Override
    public void run() {
        assertArrayEquals(
                "test message",
                new String[]{"1"},
                new String[]{"2"});
    }
}
