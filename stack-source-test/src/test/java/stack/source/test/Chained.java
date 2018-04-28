package stack.source.test;

class Chained implements Runnable {

    @Override
    public void run() {
        new Chained()
                .nothing1()
                .nothing2()
                .fail("what?")
                .nothing3()
                .fail("more?")
                .fail("and more?")
                .fail("and more more?");
    }

    private Chained fail(String message) throws TestException {
        throw new TestException(message);
    }

    private Chained nothing1() {
        return this;
    }

    private Chained nothing2() {
        return this;
    }

    private Chained nothing3() {
        return this;
    }
}
