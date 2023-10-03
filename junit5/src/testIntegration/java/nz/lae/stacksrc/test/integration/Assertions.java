package nz.lae.stacksrc.test.integration;

import static nz.lae.stacksrc.test.Assertions.assertStackTrace;

import jakarta.xml.bind.JAXB;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import nz.lae.stacksrc.junit5.DecoratedAssertionError;

class Assertions {
  private Assertions() {}

  static void assertOpenTestReport(Path testReportDir, String expectedStackTrace)
      throws IOException {
    try (var stream = Files.list(testReportDir)) {

      var report =
          JAXB.unmarshal(
              stream
                  .filter(it -> it.getFileName().toString().startsWith("junit-platform-events-"))
                  .findFirst()
                  .orElseThrow()
                  .toFile(),
              OpenTestReport.class);

      assertStackTrace(
          expectedStackTrace,
          report.finished.stream()
              .flatMap(fin -> Optional.ofNullable(fin.result).map(res -> res.throwable).stream())
              .filter(err -> DecoratedAssertionError.class.getName().equals(err.type))
              .map(err -> err.stackTrace)
              .findFirst()
              .orElseThrow());
    }
  }
}
