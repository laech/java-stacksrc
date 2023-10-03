package nz.lae.stacksrc.test.integration;

import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;
import static nz.lae.stacksrc.test.Assertions.assertStackTrace;

import jakarta.xml.bind.JAXB;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class GradleIT {

  @Test
  void checkGradleTestReportContainsCodeSnippet() throws Exception {

    var gradlew =
        Paths.get(
            requireNonNull(
                    getClass()
                        .getResource(
                            getProperty("os.name").startsWith("Windows")
                                ? "/gradle/gradlew.bat"
                                : "/gradle/gradlew"))
                .toURI());

    Processes.run(gradlew.getParent(), gradlew.toString(), "clean", "test");

    var report =
        JAXB.unmarshal(
            gradlew
                .resolveSibling(
                    "build/test-results/test/TEST-nz.lae.stacksrc.test.integration.GradleTest.xml")
                .toFile(),
            TestReport.class);

    assertStackTrace(
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

""",
        report.testCase.failure.message);
  }
}
