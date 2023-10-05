package nz.lae.stacksrc.test.integration;

import static java.lang.System.getProperty;
import static nz.lae.stacksrc.test.Assertions.assertSingleFailureJUnitReport;
import static nz.lae.stacksrc.test.Assertions.assertSingleFailureOpenTestReport;

import java.nio.file.Paths;
import nz.lae.stacksrc.test.Processes;
import org.junit.jupiter.api.Test;

class GradleJUnit5IT {

  @Test
  void checkGradleTestReportContainsCodeSnippet() throws Exception {

    var gradlew =
        Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
            .resolve("../../../../integration/gradle")
            .resolve(getProperty("os.name").startsWith("Windows") ? "gradlew.bat" : "gradlew")
            .normalize();

    Processes.run(gradlew.getParent(), gradlew.toString(), "-q", "clean", "test");

    var expectedStackTrace =
        """
nz.lae.stacksrc.DecoratedAssertionError:
org.opentest4j.AssertionFailedError: example failure
	at org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:38)
	at org.junit.jupiter.api.Assertions.fail(Assertions.java:134)
	at nz.lae.stacksrc.test.integration.GradleTest.run(GradleTest.java:11)

	    9    @Test
	   10    void run() {
	-> 11      fail("example failure");
	   12    }
	   13  }

""";

    var reportDir = gradlew.resolveSibling("build/test-results/test");
    assertSingleFailureOpenTestReport(reportDir, expectedStackTrace);
    assertSingleFailureJUnitReport(
        reportDir.resolve("TEST-nz.lae.stacksrc.test.integration.GradleTest.xml"),
        expectedStackTrace);
  }
}
