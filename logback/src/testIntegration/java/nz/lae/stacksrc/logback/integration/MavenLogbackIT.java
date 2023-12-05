package nz.lae.stacksrc.logback.integration;

import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;
import static nz.lae.stacksrc.test.Assertions.assertStackTrace;

import java.nio.file.Files;
import java.nio.file.Paths;
import nz.lae.stacksrc.test.Processes;
import org.junit.jupiter.api.Test;

class MavenLogbackIT {

  @Test
  void checkLogbackOutputContainsCodeSnippet() throws Exception {

    var mvnw =
        Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
            .resolve("../../../../integration/maven")
            .resolve(getProperty("os.name").startsWith("Windows") ? "mvnw.cmd" : "mvnw")
            .normalize();

    Processes.run(
        requireNonNull(mvnw.getParent()), mvnw.toString(), "-U", "-q", "-B", "clean", "test");

    var expectedStackTrace =
        """
This is a test
java.lang.RuntimeException: This is an example error.
	at nz.lae.stacksrc.test.integration.MavenLogbackTest.run(MavenLogbackTest.java:14)

	   12    void run() {
	   13      try {
	-> 14        throw new RuntimeException("This is an example error.");
	   15      } catch (RuntimeException e) {
	   16        logger.error("This is a test", e);

""";

    assertStackTrace(
        expectedStackTrace, Files.readString(mvnw.resolveSibling("target/output.log")));
  }
}
