package nz.lae.stacksrc.test.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenLogbackExample {

  private static final Logger logger = LoggerFactory.getLogger(MavenLogbackExample.class);

  public static void main(String[] args) {
    try {
      throw new RuntimeException("This is an example error.");
    } catch (RuntimeException e) {
      logger.error("This is a test", e);
    }
  }
}
