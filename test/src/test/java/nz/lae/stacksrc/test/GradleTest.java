package nz.lae.stacksrc.test;

import static java.lang.System.getProperty;
import static java.util.concurrent.TimeUnit.MINUTES;
import static nz.lae.stacksrc.test.Assertions.assertStackTrace;

import jakarta.xml.bind.JAXB;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

class GradleTest {

  @Test
  void checkGradleTestReportContainsCodeSnippet() throws Exception {

    var moduleRoot =
        Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
            .toAbsolutePath()
            .resolve("../..")
            .normalize();

    var projectRoot = moduleRoot.resolve("..").normalize();
    runProcess(
        projectRoot,
        projectRoot
            .resolve(getProperty("os.name").startsWith("Windows") ? "mvnw.cmd" : "mvnw")
            .toString(),
        "install",
        "-pl",
        "junit5");

    var gradleRoot = moduleRoot.resolve("gradle");
    runProcess(
        gradleRoot,
        gradleRoot
            .resolve(getProperty("os.name").startsWith("Windows") ? "gradlew.bat" : "gradlew")
            .toString(),
        "--no-daemon",
        "clean",
        "test");

    var testSuite =
        JAXB.unmarshal(
            gradleRoot
                .resolve("build/test-results/test/TEST-nz.lae.stacksrc.test.gradle.GradleTest.xml")
                .toFile(),
            TestSuite.class);

    assertStackTrace(
        """
nz.lae.stacksrc.junit5.DecoratedAssertionError:
org.opentest4j.AssertionFailedError: example failure
	at org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:38)
	at org.junit.jupiter.api.Assertions.fail(Assertions.java:134)
	at nz.lae.stacksrc.test.gradle.GradleTest.run(GradleTest.java:11)

	    9    @Test
	   10    void run() {
	-> 11      fail("example failure");
	   12    }
	   13  }

""",
        testSuite.testCase.failure.message);
  }

  private void runProcess(Path directory, String... command)
      throws IOException, InterruptedException {

    var builder = new ProcessBuilder(command).directory(directory.toFile()).inheritIO();
    var process = builder.start();
    try {
      if (!process.waitFor(2, MINUTES)) {
        throw new AssertionFailedError("Timed out.");
      }
    } finally {
      process.destroyForcibly();
    }
    if (process.exitValue() != 0) {
      throw new AssertionFailedError("Process failed, check the log for error.");
    }
  }
}
