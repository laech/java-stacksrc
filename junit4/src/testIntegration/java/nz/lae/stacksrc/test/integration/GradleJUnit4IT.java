package nz.lae.stacksrc.test.integration;

import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;
import static nz.lae.stacksrc.test.Assertions.assertSingleFailureJUnitReport;

import java.nio.file.Paths;
import nz.lae.stacksrc.test.Processes;
import org.junit.Test;

public class GradleJUnit4IT {

  @Test
  public void checkGradleTestReportContainsCodeSnippet() throws Exception {

    var gradlew =
        Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
            .resolve("../../../../integration/gradle")
            .resolve(getProperty("os.name").startsWith("Windows") ? "gradlew.bat" : "gradlew")
            .normalize();

    Processes.run(requireNonNull(gradlew.getParent()), gradlew.toString(), "-q", "clean", "test");

    var expectedStackTrace =
        """
java.lang.AssertionError: example failure
	at org.junit.Assert.fail(Assert.java:89)
	at nz.lae.stacksrc.test.integration.GradleJUnit4Test.run(GradleJUnit4Test.java:16)

	   14    @Test
	   15    public void run() {
	-> 16      fail("example failure");
	   17    }
	   18  }

""";

    var reportDir = gradlew.resolveSibling("build/test-results/test");
    assertSingleFailureJUnitReport(
        reportDir.resolve("TEST-nz.lae.stacksrc.test.integration.GradleJUnit4Test.xml"),
        expectedStackTrace);
  }
}
