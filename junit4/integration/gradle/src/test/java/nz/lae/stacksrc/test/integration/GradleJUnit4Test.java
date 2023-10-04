package nz.lae.stacksrc.test.integration;

import static org.junit.Assert.fail;

import nz.lae.stacksrc.junit4.ErrorDecorator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

public class GradleJUnit4Test {

  @Rule public final TestRule errorDecorator = new ErrorDecorator();

  @Test
  public void run() {
    fail("example failure");
  }
}
