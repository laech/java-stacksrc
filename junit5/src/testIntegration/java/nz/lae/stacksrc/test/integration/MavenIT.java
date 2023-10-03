package nz.lae.stacksrc.test.integration;

import static java.lang.System.getProperty;
import static java.lang.System.lineSeparator;
import static java.util.Objects.requireNonNull;
import static nz.lae.stacksrc.test.Assertions.assertStackTrace;

import jakarta.xml.bind.JAXB;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class MavenIT {

  @Test
  void checkMavenTestReportContainsCodeSnippet() throws Exception {

    var mvnw =
        Paths.get(
            requireNonNull(
                    getClass()
                        .getResource(
                            getProperty("os.name").startsWith("Windows")
                                ? "/maven/mvnw.cmd"
                                : "/maven/mvnw"))
                .toURI());

    Processes.run(mvnw.getParent(), mvnw.toString(), "clean", "test");

    var report =
        JAXB.unmarshal(
            mvnw.resolveSibling(
                    "target/surefire-reports/TEST-nz.lae.stacksrc.test.integration.MavenTest.xml")
                .toFile(),
            TestReport.class);

    assertStackTrace(
        """
org.opentest4j.AssertionFailedError: example failure
 at org.junit.jupiter.api.AssertionUtils.fail(AssertionUtils.java:38)
 at org.junit.jupiter.api.Assertions.fail(Assertions.java:134)
 at nz.lae.stacksrc.test.integration.MavenTest.run(MavenTest.java:11)

     9    @Test
    10    void run() {
 -> 11      fail("example failure");
    12    }
    13  }

""",
        report.testCase.failure.message.replaceAll("\r?\n", lineSeparator()));
  }
}
