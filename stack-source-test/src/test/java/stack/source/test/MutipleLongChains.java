package stack.source.test;

import java.util.function.Supplier;

final class MutipleLongChains implements Runnable {

    private MutipleLongChains test(String msg) {
        return this;
    }

    private MutipleLongChains fail(Supplier<String> msg) {
        throw new TestException("bob");
    }

    @Override
    public void run() {

        Helper helper1 = new Helper();
        Helper helper2 = helper1
                .test("x")
                .test("x")
                .test("x");

        test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x");

        test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x")
                .test("x");
    }

    private static class Helper {
        private Helper test(String msg) {
            throw new TestException("bob");
        }
    }
}