package nz.lae.stacksrc.logback.integration;

import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import nz.lae.stacksrc.test.Processes;
import org.junit.jupiter.api.Test;

class GradleLogbackExecIT {

  @Test
  void checkExecOutputContainsCodeSnippet() throws Exception {
    var gradlew = getPathToGradleWrapper();
    var output =
        Processes.run(
            requireNonNull(gradlew.getParent()), gradlew.toString(), "-q", "clean", "run");

    var expectedStackTrace =
        """
This is a test
java.lang.RuntimeException: This is an example error.
	at nz.lae.stacksrc.test.integration.GradleLogbackExec.main(GradleLogbackExec.java:12)

	   10    public static void main(String[] args) {
	   11      try {
	-> 12        throw new RuntimeException("This is an example error.");
	   13      } catch (RuntimeException e) {
	   14        logger.error("This is a test", e);


""";

    assertThat(output).isEqualTo(expectedStackTrace);
  }

  @Test
  void checkTestOutputContainsCodeSnippet() throws Exception {
    var gradlew = getPathToGradleWrapper();
    Processes.run(requireNonNull(gradlew.getParent()), gradlew.toString(), "-q", "clean", "test");

    var expectedStackTrace =
        """
This is a test
java.lang.RuntimeException: This is an example error.
	at nz.lae.stacksrc.test.integration.GradleLogbackExecTest.run(GradleLogbackExecTest.java:14)

	   12    void run() {
	   13      try {
	-> 14        throw new RuntimeException("This is an example error.");
	   15      } catch (RuntimeException e) {
	   16        logger.error("This is a test", e);


""";

    var actualStackTrace = Files.readString(gradlew.resolveSibling("build/test-output.log"));
    assertThat(actualStackTrace).startsWith(expectedStackTrace);
  }

  private Path getPathToGradleWrapper() throws URISyntaxException {
    return Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
        .resolve("../../../../integration/gradle-exec")
        .resolve(getProperty("os.name").startsWith("Windows") ? "gradlew.bat" : "gradlew")
        .normalize();
  }
}
