package stack.source.junit4;

import static org.junit.Assert.fail;

final class Fail implements Runnable {
    @Override
    public void run() {
        fail("testing failure");
    }
}
