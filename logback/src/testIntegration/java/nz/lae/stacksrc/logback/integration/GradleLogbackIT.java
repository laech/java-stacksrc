package nz.lae.stacksrc.logback.integration;

import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;
import static nz.lae.stacksrc.test.Assertions.assertStackTrace;

import java.nio.file.Files;
import java.nio.file.Paths;
import nz.lae.stacksrc.test.Processes;
import org.junit.jupiter.api.Test;

class GradleLogbackIT {

  @Test
  void checkLogbackOutputContainsCodeSnippet() throws Exception {

    var gradlew =
        Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
            .resolve("../../../../integration/gradle")
            .resolve(getProperty("os.name").startsWith("Windows") ? "gradlew.bat" : "gradlew")
            .normalize();

    Processes.run(requireNonNull(gradlew.getParent()), gradlew.toString(), "-q", "clean", "run");

    var expectedStackTrace =
        """
This is a test
java.lang.RuntimeException: This is an example error.
	at nz.lae.stacksrc.test.integration.GradleLogbackExample.main(GradleLogbackExample.java:12)

	   10    public static void main(String[] args) {
	   11      try {
	-> 12        throw new RuntimeException("This is an example error.");
	   13      } catch (RuntimeException e) {
	   14        logger.error("This is a test", e);

""";

    assertStackTrace(
        expectedStackTrace, Files.readString(gradlew.resolveSibling("build/output.log")));
  }
}
