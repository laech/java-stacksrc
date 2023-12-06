package nz.lae.stacksrc.test.integration;

import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;
import static nz.lae.stacksrc.test.Assertions.assertSingleFailureJUnitReportHasExpectedStackTracePrefix;

import java.nio.file.Paths;
import nz.lae.stacksrc.test.Processes;
import org.testng.annotations.Test;

class MavenTestngIT {

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
java.lang.AssertionError: example failure
	at nz.lae.stacksrc.test.integration.MavenTestngTest.run(MavenTestngTest.java:9)

	    7    @Test
	    8    public void run() {
	->  9      assert false : "example failure";
	   10    }
	   11  }

""";

    var reportDir = mvnw.resolveSibling("target/surefire-reports");
    assertSingleFailureJUnitReportHasExpectedStackTracePrefix(
        reportDir.resolve("TEST-nz.lae.stacksrc.test.integration.MavenTestngTest.xml"),
        expectedStackTrace);
  }
}
