package nz.lae.stafcksrc.test.integration;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GradleLogbackTest {

  private static final Logger logger = LoggerFactory.getLogger(GradleLogbackTest.class);

  @Test
  void run() {
    try {
      throw new RuntimeException("This is an example error.");
    } catch (RuntimeException e) {
      logger.error("This is a test", e);
    }
  }
}
