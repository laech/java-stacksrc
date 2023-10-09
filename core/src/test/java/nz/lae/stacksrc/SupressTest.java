package nz.lae.stacksrc;

import static nz.lae.stacksrc.test.Assertions.assertStackTrace;
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
	at nz.lae.stacksrc.SupressTest.doThrow(SupressTest.java:11)

	   10    private void doThrow() {
	-> 11      var root = new AssertionError("rethrown");
	   12      try {
	   13        throw new IllegalArgumentException("test1");


	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:53)
	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:35)
	at org.junit.jupiter.api.Assertions.assertThrows(Assertions.java:3111)
	at nz.lae.stacksrc.SupressTest.run(SupressTest.java:27)

	   25    @Test
	   26    void run() {
	-> 27      var exception = assertThrows(AssertionError.class, this::doThrow);
	   28      var expected =
	   29          ""\"


	Suppressed: java.lang.IllegalArgumentException: test1
		at nz.lae.stacksrc.SupressTest.doThrow(SupressTest.java:13)

		   11      var root = new AssertionError("rethrown");
		   12      try {
		-> 13        throw new IllegalArgumentException("test1");
		   14      } catch (IllegalArgumentException e) {
		   15        root.addSuppressed(e);


		... 4 more
	Suppressed: java.lang.IllegalArgumentException: test2
		at nz.lae.stacksrc.SupressTest.doThrow(SupressTest.java:18)

		   16      }
		   17      try {
		-> 18        throw new IllegalArgumentException("test2");
		   19      } catch (IllegalArgumentException e) {
		   20        root.addSuppressed(e);


		... 4 more""";

    assertStackTrace(expected, StackTraceDecorator.get().decorate(exception, getClass()));
  }
}
