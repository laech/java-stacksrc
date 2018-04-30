package stack.source.test;

final class DeepCalls implements Runnable {

    @Override
    public void run() {
        call1();
    }

    private void call1() {
        call2();
    }

    private void call2() {
        call3();
    }

    private void call3() {
        call4();
    }

    private void call4() {
        call5();
    }

    private void call5() {
        call6();
    }

    private void call6() {
        call7();
    }

    private void call7() {
        call8();
    }

    private void call8() {
        call9();
    }

    private void call9() {
        throw new TestException("test");
    }
}
