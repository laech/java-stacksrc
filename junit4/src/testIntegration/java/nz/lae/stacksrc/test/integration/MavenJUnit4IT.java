package nz.lae.stacksrc.test.integration;

import static java.lang.System.getProperty;
import static nz.lae.stacksrc.test.Assertions.assertStackTrace;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.xml.bind.JAXB;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class MavenJUnit4IT {

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
nz.lae.stacksrc.junit4.DecoratedAssertionError:
java.lang.AssertionError: example failure
	at org.junit.Assert.fail(Assert.java:89)
	at nz.lae.stacksrc.test.integration.MavenJUnit4Test.run(MavenJUnit4Test.java:16)

	   14    @Test
	   15    public void run() {
	-> 16      fail("example failure");
	   17    }
	   18  }

""";

    var reportDir = mvnw.resolveSibling("target/surefire-reports");
    assertJUnitReport(reportDir, expectedStackTrace);
  }

  private static void assertJUnitReport(Path reportDir, String expectedStackTrace) {
    var report =
        JAXB.unmarshal(
            reportDir.resolve("TEST-nz.lae.stacksrc.test.integration.MavenJUnit4Test.xml").toFile(),
            JUnitTestReport.class);

    assertEquals("example failure", report.testcase.failure.message);
    assertStackTrace(expectedStackTrace, report.testcase.failure.stackTrace);
  }
}
