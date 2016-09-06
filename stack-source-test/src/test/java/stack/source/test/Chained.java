package stack.source.test;

class Chained implements Runnable {

    @Override
    public void run() {
        new Chained()
                .nothing1("blah")
                .nothing2("meh")
                .fail("what?")
                .nothing3();
    }

    private Chained fail(String message) throws TestException {
        throw new TestException(message);
    }

    private Chained nothing1(
            @SuppressWarnings("UnusedParameters") String ignored) {
        return this;
    }

    private Chained nothing2(
            @SuppressWarnings("UnusedParameters") String ignored) {
        return this;
    }

    private Chained nothing3() {
        return this;
    }
}
