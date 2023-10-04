package nz.lae.stacksrc.test.integration;

import static java.lang.System.getProperty;
import static nz.lae.stacksrc.test.Assertions.assertStackTrace;

import jakarta.xml.bind.JAXB;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class GradleJUnit4IT {

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
nz.lae.stacksrc.junit4.DecoratedAssertionError:
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
    assertJunitReport(reportDir, expectedStackTrace);
  }

  private static void assertJunitReport(Path testReportDir, String stackTrace) {
    var report =
        JAXB.unmarshal(
            testReportDir
                .resolve("TEST-nz.lae.stacksrc.test.integration.GradleJUnit4Test.xml")
                .toFile(),
            JUnitTestReport.class);

    assertStackTrace(stackTrace, report.testcase.failure.stackTrace);

    // Unlike maven, gradle populates the message field differently
    assertStackTrace(stackTrace, report.testcase.failure.message);
  }
}
