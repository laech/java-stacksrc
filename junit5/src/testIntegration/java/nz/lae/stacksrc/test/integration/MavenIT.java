package nz.lae.stacksrc.test.integration;

import static java.lang.System.getProperty;
import static nz.lae.stacksrc.test.Assertions.assertStackTrace;
import static nz.lae.stacksrc.test.integration.Assertions.assertOpenTestReport;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.xml.bind.JAXB;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class MavenIT {

  @Test
  void checkMavenTestReportContainsCodeSnippet() throws Exception {

    var mvnw =
        Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
            .resolve("../../../../integration/maven")
            .resolve(getProperty("os.name").startsWith("Windows") ? "mvnw.cmd" : "mvnw")
            .normalize();

    Processes.run(mvnw.getParent(), mvnw.toString(), "-q", "-B", "clean", "test");

    var expectedStackTrace =
        """
nz.lae.stacksrc.junit5.DecoratedAssertionError:
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
    assertJUnitReport(reportDir, expectedStackTrace);
    assertOpenTestReport(reportDir, expectedStackTrace);
  }

  private static void assertJUnitReport(Path reportDir, String expectedStackTrace) {
    var report =
        JAXB.unmarshal(
            reportDir.resolve("TEST-nz.lae.stacksrc.test.integration.MavenTest.xml").toFile(),
            JUnitTestReport.class);

    assertEquals("example failure", report.testcase.failure.message);
    assertStackTrace(expectedStackTrace, report.testcase.failure.stackTrace);
  }
}
