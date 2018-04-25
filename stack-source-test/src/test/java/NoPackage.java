import stack.source.test.TestException;

final class NoPackage implements Runnable {

    @Override
    public void run() {
        throw new TestException("no package");
    }

}
