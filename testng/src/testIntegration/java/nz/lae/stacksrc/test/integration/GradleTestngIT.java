package nz.lae.stacksrc.test.integration;

import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;
import static nz.lae.stacksrc.test.Assertions.assertSingleFailureJUnitReport;

import java.nio.file.Paths;
import nz.lae.stacksrc.test.Processes;
import org.testng.annotations.Test;

class GradleTestngIT {

  @Test
  void checkGradleTestReportContainsCodeSnippet() throws Exception {

    var gradlew =
        Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
            .resolve("../../../../integration/gradle")
            .resolve(getProperty("os.name").startsWith("Windows") ? "gradlew.bat" : "gradlew")
            .normalize();

    Processes.run(requireNonNull(gradlew.getParent()), gradlew.toString(), "-q", "clean", "test");

    var expectedStackTrace =
        """
java.lang.AssertionError: example failure
	at nz.lae.stacksrc.test.integration.GradleTestngTest.run(GradleTestngTest.java:9)

	    7    @Test
	    8    public void run() {
	->  9      assert false : "example failure";
	   10    }
	   11  }

""";

    var reportDir = gradlew.resolveSibling("build/test-results/test");
    assertSingleFailureJUnitReport(
        reportDir.resolve("TEST-nz.lae.stacksrc.test.integration.GradleTestngTest.xml"),
        expectedStackTrace);
  }
}
