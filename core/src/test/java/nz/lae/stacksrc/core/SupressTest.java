package nz.lae.stacksrc.core;

import static java.util.stream.Collectors.joining;
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
	at nz.lae.stacksrc.core.SupressTest.doThrow(SupressTest.java:12)

	   11    private void doThrow() {
	-> 12      var root = new AssertionError("rethrown");
	   13      try {
	   14        throw new IllegalArgumentException("test1");


	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:53)
	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:35)
	at org.junit.jupiter.api.Assertions.assertThrows(Assertions.java:3111)
	at nz.lae.stacksrc.core.SupressTest.run(SupressTest.java:28)

	   26    @Test
	   27    void run() {
	-> 28      var exception = assertThrows(AssertionError.class, this::doThrow);
	   29      var expected =
	   30          ""\"


	Suppressed: java.lang.IllegalArgumentException: test1
		at nz.lae.stacksrc.core.SupressTest.doThrow(SupressTest.java:14)

		   12      var root = new AssertionError("rethrown");
		   13      try {
		-> 14        throw new IllegalArgumentException("test1");
		   15      } catch (IllegalArgumentException e) {
		   16        root.addSuppressed(e);


	Suppressed: java.lang.IllegalArgumentException: test2
		at nz.lae.stacksrc.core.SupressTest.doThrow(SupressTest.java:19)

		   17      }
		   18      try {
		-> 19        throw new IllegalArgumentException("test2");
		   20      } catch (IllegalArgumentException e) {
		   21        root.addSuppressed(e);

    """;

    var actual =
        StackTraceDecorator.create()
            .decorate(exception)
            .lines()
            .filter(line -> !line.contains("java.base/"))
            .filter(line -> !line.contains("jdk.proxy1/"))
            .filter(line -> !line.contains("org.junit.platform"))
            .filter(line -> !line.contains("org.junit.jupiter.engine"))
            .filter(line -> !line.contains("org.gradle"))
            .filter(line -> !line.contains("\t..."))
            .collect(joining("\n"));

    assertStackTrace(expected, actual);
  }
}
