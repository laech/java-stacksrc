package stack.source.test;

final class MultilineThrow implements Runnable {

    @Override
    public void run() {
        throw new TestException(
                "hello world"
        );
    }

}
