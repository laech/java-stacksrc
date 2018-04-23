package stack.source.junit5;

import static org.junit.jupiter.api.Assertions.fail;

final class Fail implements Runnable {
    @Override
    public void run() {
        fail("testing failure");
    }
}
