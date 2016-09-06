package stack.source.test;

class ReturnFailure implements Runnable {

    @Override
    public void run() {
        System.err.println(hi());
    }

    private String hi() {
        return bye();
    }

    private String bye() {
        throw new TestException("testing");
    }
}
