package nz.lae.stacksrc;

import static nz.lae.stacksrc.Throwables.pruneStackTrace;
import static nz.lae.stacksrc.test.Assertions.assertStackTraceHasExpectedPrefix;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SupressTest {

  private void doThrow() {
    var root = new AssertionError("rethrown");
    try {
      throw new IllegalArgumentException("test1");
    } catch (IllegalArgumentException e) {
      root.addSuppressed(e);
    }
    try {
      throw new IllegalArgumentException("test2");
    } catch (IllegalArgumentException e) {
      root.addSuppressed(e);
    }
    throw root;
  }

  @Test
  void run() {
    var exception = assertThrows(AssertionError.class, this::doThrow);
    var expected =
        """
java.lang.AssertionError: rethrown
	at nz.lae.stacksrc.SupressTest.doThrow(SupressTest.java:12)

	   11    private void doThrow() {
	-> 12      var root = new AssertionError("rethrown");
	   13      try {
	   14        throw new IllegalArgumentException("test1");


	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:53)
	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:35)
	at org.junit.jupiter.api.Assertions.assertThrows(Assertions.java:3111)
	at nz.lae.stacksrc.SupressTest.run(SupressTest.java:28)

	   26    @Test
	   27    void run() {
	-> 28      var exception = assertThrows(AssertionError.class, this::doThrow);
	   29      var expected =
	   30          ""\"


	Suppressed: java.lang.IllegalArgumentException: test1
		at nz.lae.stacksrc.SupressTest.doThrow(SupressTest.java:14)

		   12      var root = new AssertionError("rethrown");
		   13      try {
		-> 14        throw new IllegalArgumentException("test1");
		   15      } catch (IllegalArgumentException e) {
		   16        root.addSuppressed(e);


		... 4 more
	Suppressed: java.lang.IllegalArgumentException: test2
		at nz.lae.stacksrc.SupressTest.doThrow(SupressTest.java:19)

		   17      }
		   18      try {
		-> 19        throw new IllegalArgumentException("test2");
		   20      } catch (IllegalArgumentException e) {
		   21        root.addSuppressed(e);


		... 4 more""";

    pruneStackTrace(exception, getClass());
    assertStackTraceHasExpectedPrefix(expected, StackTraceDecorator.get().decorate(exception));
  }
}
