package nz.lae.stacksrc.test;

import static java.lang.Math.min;
import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.xml.bind.JAXB;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import nz.lae.stacksrc.DecoratedAssertionError;

public class Assertions {
  private Assertions() {}

  public static void assertStackTraceHasExpectedPrefix(String expected, DecoratedAssertionError e) {
    assertStackTraceHasExpectedPrefix(expected, getStackTraceAsString(e));
  }

  public static void assertStackTraceHasExpectedPrefix(String expected, String actual) {
    expected = expected.replaceAll("\r?\n", lineSeparator());
    actual = actual.replaceAll("\r?\n", lineSeparator());
    actual = actual.substring(0, min(expected.length(), actual.length()));
    assertEquals(expected, actual);
  }

  public static void assertStackTraceExactly(String expected, String actual) {
    expected = expected.replaceAll("\r?\n", lineSeparator());
    actual = actual.replaceAll("\r?\n", lineSeparator());
    assertEquals(expected, actual);
  }

  private static String getStackTraceAsString(Throwable e) {
    var stringWriter = new StringWriter();
    var printWriter = new PrintWriter(stringWriter);
    e.printStackTrace(printWriter);
    printWriter.flush();
    return stringWriter.toString();
  }

  public static void assertSingleFailureOpenTestReportExactly(
      Path testReportDir, String expectedStackTrace) throws IOException {
    try (var stream = Files.list(testReportDir)) {

      var report =
          JAXB.unmarshal(
              stream
                  .filter(it -> it.getFileName().toString().startsWith("junit-platform-events-"))
                  .findFirst()
                  .orElseThrow()
                  .toFile(),
              OpenTestReport.class);

      assertStackTraceExactly(
          expectedStackTrace,
          report.finished.stream()
              .flatMap(fin -> Optional.ofNullable(fin.result).map(res -> res.throwable).stream())
              .filter(err -> err.type.startsWith("nz.lae.stacksrc."))
              .map(err -> err.stackTrace)
              .findFirst()
              .orElseThrow());
    }
  }

  public static void assertSingleFailureJUnitReportHasExpectedStackTracePrefix(
      Path reportFile, String expectedStackTrace) {
    var report = JAXB.unmarshal(reportFile.toFile(), JUnitTestReport.class);
    assertStackTraceHasExpectedPrefix(expectedStackTrace, report.testcase.failure.stackTrace);
  }

  public static void assertSingleFailureJUnitReportExactly(
      Path reportFile, String expectedStackTrace) {
    var report = JAXB.unmarshal(reportFile.toFile(), JUnitTestReport.class);
    assertStackTraceExactly(expectedStackTrace, report.testcase.failure.stackTrace);
  }
}
