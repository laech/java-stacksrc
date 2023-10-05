package nz.lae.stacksrc.test.integration;

import static java.lang.System.getProperty;
import static nz.lae.stacksrc.test.Assertions.assertSingleFailureJUnitReport;

import java.nio.file.Paths;
import nz.lae.stacksrc.test.Processes;
import org.junit.jupiter.api.Test;

class MavenJUnit4IT {

  @Test
  void checkMavenTestReportContainsCodeSnippet() throws Exception {

    var mvnw =
        Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
            .resolve("../../../../integration/maven")
            .resolve(getProperty("os.name").startsWith("Windows") ? "mvnw.cmd" : "mvnw")
            .normalize();

    Processes.run(mvnw.getParent(), mvnw.toString(), "-U", "-q", "-B", "clean", "test");

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
    assertSingleFailureJUnitReport(
        reportDir.resolve("TEST-nz.lae.stacksrc.test.integration.MavenJUnit4Test.xml"),
        expectedStackTrace);
  }
}
