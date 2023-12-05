package nz.lae.stacksrc.example;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

class TestngExampleTest {

  @Test
  void plainAssert() {
    assert false;
  }

  @Test
  void compareStrings() {
    assertEquals("bob", "bab");
  }
}
