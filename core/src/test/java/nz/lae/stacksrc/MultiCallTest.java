package nz.lae.stacksrc;

import static java.util.Arrays.asList;
import static nz.lae.stacksrc.test.Assertions.assertStackTrace;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class MultiCallTest {

  private void doThrow() {
    assertList(asList("bob", "bob"));
    assertList(asList("abc", "def"));
  }

  private void assertString(String s) {
    if (s.equals("bob")) {
      throw new AssertionError("bob");
    }
  }

  private void assertList(List<String> list) {
    list.forEach(
        s ->
            Optional.of(s.toLowerCase())
                .ifPresent(
                    ss -> {
                      if (ss.length() > 1000) {
                        throw new AssertionError("no");
                      }
                      assertString(ss);
                    }));
  }

  @Test
  void run() {
    var exception = assertThrows(AssertionError.class, this::doThrow);
    var expected =
        """
java.lang.AssertionError: bob
	at nz.lae.stacksrc.MultiCallTest.assertString(MultiCallTest.java:20)

	   18    private void assertString(String s) {
	   19      if (s.equals("bob")) {
	-> 20        throw new AssertionError("bob");
	   21      }
	   22    }


	at nz.lae.stacksrc.MultiCallTest.lambda$assertList$0(MultiCallTest.java:33)

	   31                          throw new AssertionError("no");
	   32                        }
	-> 33                        assertString(ss);
	   34                      }));
	   35    }


	at java.base/java.util.Optional.ifPresent(Optional.java:178)
	at nz.lae.stacksrc.MultiCallTest.lambda$assertList$1(MultiCallTest.java:28)

	   26          s ->
	   27              Optional.of(s.toLowerCase())
	-> 28                  .ifPresent(
	   29                      ss -> {
	   30                        if (ss.length() > 1000) {


	at java.base/java.util.Arrays$ArrayList.forEach(Arrays.java:4204)
	at nz.lae.stacksrc.MultiCallTest.assertList(MultiCallTest.java:25)

	   24    private void assertList(List<String> list) {
	-> 25      list.forEach(
	   26          s ->
	   27              Optional.of(s.toLowerCase())


	at nz.lae.stacksrc.MultiCallTest.doThrow(MultiCallTest.java:14)

	   13    private void doThrow() {
	-> 14      assertList(asList("bob", "bob"));
	   15      assertList(asList("abc", "def"));
	   16    }


	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:53)
	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:35)
	at org.junit.jupiter.api.Assertions.assertThrows(Assertions.java:3111)
	at nz.lae.stacksrc.MultiCallTest.run(MultiCallTest.java:39)

	   37    @Test
	   38    void run() {
	-> 39      var exception = assertThrows(AssertionError.class, this::doThrow);
	   40      var expected =
	   41          ""\"

""";
    assertStackTrace(expected, StackTraceDecorator.get().decorate(exception));
  }
}
