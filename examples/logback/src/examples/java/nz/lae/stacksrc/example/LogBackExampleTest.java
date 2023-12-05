package nz.lae.stacksrc.example;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LogBackExampleTest {

  private static final Logger logger = LoggerFactory.getLogger(LogBackExampleTest.class);

  @Test
  void run() {
    var cause = new Exception("This is a cause", new AssertionError("This is also a cause"));
    var suppressed1 = new IOException("This is suppressed");
    var suppressed2 = new IllegalArgumentException("This is suppressed too");
    var root = new RuntimeException("Test", cause);
    root.addSuppressed(suppressed1);
    root.addSuppressed(suppressed2);
    logger.error("This is an example error", root);
  }
}
