package nz.lae.stacksrc.test.integration;

import static java.lang.System.getProperty;
import static nz.lae.stacksrc.test.Assertions.assertStackTrace;

import jakarta.xml.bind.JAXB;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class GradleIT {

  @Test
  void checkGradleTestReportContainsCodeSnippet() throws Exception {

    var gradlew =
        Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
            .resolve("../../../../integration/gradle")
            .resolve(getProperty("os.name").startsWith("Windows") ? "gradlew.bat" : "gradlew")
            .normalize();

    Processes.run(gradlew.getParent(), gradlew.toString(), "-q", "clean", "test");

    var report =
        JAXB.unmarshal(
            gradlew
                .resolveSibling(
                    "build/test-results/test/TEST-nz.lae.stacksrc.test.integration.GradleTest.xml")
                .toFile(),
            TestReport.class);

    var stackTrace =
        """
nz.lae.stacksrc.junit5.DecoratedAssertionError:
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
    assertStackTrace(stackTrace, report.testCase.failure.stackTrace);

    // Unlike maven, gradle populates the message field differently
    assertStackTrace(stackTrace, report.testCase.failure.message);
  }
}
