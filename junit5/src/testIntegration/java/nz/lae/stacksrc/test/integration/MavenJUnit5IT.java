package nz.lae.stacksrc.test.integration;

import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;
import static nz.lae.stacksrc.test.Assertions.assertSingleFailureJUnitReportExactly;
import static nz.lae.stacksrc.test.Assertions.assertSingleFailureOpenTestReportExactly;

import java.nio.file.Paths;
import nz.lae.stacksrc.test.Processes;
import org.junit.jupiter.api.Test;

class MavenJUnit5IT {

  @Test
  void checkMavenTestReportContainsCodeSnippet() throws Exception {

    var mvnw =
        Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
            .resolve("../../../../integration/maven")
            .resolve(getProperty("os.name").startsWith("Windows") ? "mvnw.cmd" : "mvnw")
            .normalize();

    Processes.run(
        requireNonNull(mvnw.getParent()), mvnw.toString(), "-U", "-q", "-B", "clean", "test");

    var expectedStackTrace =
        """
org.opentest4j.AssertionFailedError: example failure
	at org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:38)
	at org.junit.jupiter.api.Assertions.fail(Assertions.java:134)
	at nz.lae.stacksrc.test.integration.MavenTest.run(MavenTest.java:11)

	    9    @Test
	   10    void run() {
	-> 11      fail("example failure");
	   12    }
	   13  }



""";

    var reportDir = mvnw.resolveSibling("target/surefire-reports");
    assertSingleFailureOpenTestReportExactly(reportDir, expectedStackTrace);
    assertSingleFailureJUnitReportExactly(
        reportDir.resolve("TEST-nz.lae.stacksrc.test.integration.MavenTest.xml"),
        expectedStackTrace);
  }
}
