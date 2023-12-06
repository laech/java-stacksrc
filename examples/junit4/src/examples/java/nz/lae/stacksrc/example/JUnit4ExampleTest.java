package nz.lae.stacksrc.example;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.stream.IntStream;
import nz.lae.stacksrc.junit4.ErrorDecorator;
import org.junit.Rule;
import org.junit.Test;

public class JUnit4ExampleTest {

  @Rule public final ErrorDecorator errorDecorator = new ErrorDecorator();

  @Test
  public void compareInts() {
    assertEquals(1, 2);
  }

  @Test
  public void compareStrings() {
    assertEquals("bob", "bab");
  }

  @Test
  public void compareLongStrings() {
    assertEquals(
        "2" + IntStream.range(1, 100).mapToObj(String::valueOf).collect(joining("\n")), "2");
  }

  @Test
  public void compareArrays() {
    assertArrayEquals(new String[] {"111", "112", "113"}, new String[] {"111", "112", "114"});
  }
}
