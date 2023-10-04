package nz.lae.stacksrc.example;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.stream.IntStream;
import nz.lae.stacksrc.junit5.ErrorDecorator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ErrorDecorator.class)
class JUnit5ExampleTest {

  @Test
  void compareInts() {
    assertEquals(1, 2);
  }

  @Test
  void compareStrings() {
    assertEquals("bob", "bab");
  }

  @Test
  void compareLongStrings() {
    assertEquals(
        "2" + IntStream.range(1, 100).mapToObj(String::valueOf).collect(joining("\n")), "2");
  }

  @Test
  void compareArrays() {
    assertArrayEquals(new String[] {"111", "112", "113"}, new String[] {"111", "112", "114"});
  }

  @Test
  void multipleAssertions() {
    assertAll( //
        () -> assertEquals(1, 2), //
        () -> assertSame(true, false));
  }

  @Test
  void rethrow() {
    try {
      throw new IllegalArgumentException("test");
    } catch (IllegalArgumentException e) {
      throw new AssertionError(e.getMessage(), e);
    }
  }
}
