package nz.lae.stacksrc.test.integration;

import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;
import static nz.lae.stacksrc.test.Assertions.assertSingleFailureJUnitReportHasExpectedStackTracePrefix;

import java.nio.file.Paths;
import nz.lae.stacksrc.test.Processes;
import org.junit.Test;

public class MavenJUnit4IT {

  @Test
  public void checkMavenTestReportContainsCodeSnippet() throws Exception {

    var mvnw =
        Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
            .resolve("../../../../integration/maven")
            .resolve(getProperty("os.name").startsWith("Windows") ? "mvnw.cmd" : "mvnw")
            .normalize();

    Processes.run(
        requireNonNull(mvnw.getParent()), mvnw.toString(), "-U", "-q", "-B", "clean", "test");

    var expectedStackTrace =
        """
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
    assertSingleFailureJUnitReportHasExpectedStackTracePrefix(
        reportDir.resolve("TEST-nz.lae.stacksrc.test.integration.MavenJUnit4Test.xml"),
        expectedStackTrace);
  }
}
