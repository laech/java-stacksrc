package stack.source.junit5;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

final class FailAssertArrayEquals implements Runnable {
    @Override
    public void run() {
        assertArrayEquals(new int[]{1}, new int[]{2});
    }
}
