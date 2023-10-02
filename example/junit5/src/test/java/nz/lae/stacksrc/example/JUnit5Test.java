package nz.lae.stacksrc.example;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.IntStream;
import nz.lae.stacksrc.junit5.ErrorDecorator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ErrorDecorator.class)
final class JUnit5Test {

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
}
