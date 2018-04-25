package stack.source.test;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

final class MultiCalls implements Runnable {

    private void assertString(String s) {
        if (s.equals("bob")) {
            throw new TestException("bob");
        }
    }

    private void assertList(List<String> list) {
        list.forEach(s -> Optional.of(s.toLowerCase()).ifPresent(ss -> {
            if (ss.length() > 1000) {
                throw new TestException("no");
            }
            assertString(ss);
        }));
    }

    @Override
    public void run() {
        assertList(asList("bob", "bob"));
    }
}
