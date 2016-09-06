package stack.source.test;

class ThrowNewException implements Runnable {

    @Override
    public void run() {
        throw new TestException("testing");
    }

}
