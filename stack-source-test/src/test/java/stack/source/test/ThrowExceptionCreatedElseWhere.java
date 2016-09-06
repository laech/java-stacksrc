package stack.source.test;

class ThrowExceptionCreatedElseWhere implements Runnable {

    @Override
    public void run() {
        @SuppressWarnings("UnnecessaryLocalVariable")
        TestException test = new TestException("testing");
        throw test;
    }
}
