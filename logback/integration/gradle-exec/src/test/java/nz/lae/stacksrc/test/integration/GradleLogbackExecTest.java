package nz.lae.stacksrc.test.integration;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GradleLogbackExecTest {

  private static final Logger logger = LoggerFactory.getLogger(GradleLogbackExecTest.class);

  @Test
  void run() {
    try {
      throw new RuntimeException("This is an example error.");
    } catch (RuntimeException e) {
      logger.error("This is a test", e);
    }
  }
}
