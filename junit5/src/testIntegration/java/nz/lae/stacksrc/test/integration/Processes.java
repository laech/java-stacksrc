package nz.lae.stacksrc.test.integration;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.io.IOException;
import java.nio.file.Path;
import org.opentest4j.AssertionFailedError;

class Processes {
  private Processes() {}

  static void run(Path directory, String... command) throws IOException, InterruptedException {
    var builder = new ProcessBuilder(command).directory(directory.toFile());
    var process = builder.start();
    try {
      if (!process.waitFor(2, MINUTES)) {
        throw new AssertionFailedError(
            """
            Timed out:
            stdout:
            %s
            stderr:
            %s
            """
                .formatted(
                    new String(process.getInputStream().readAllBytes(), UTF_8),
                    new String(process.getErrorStream().readAllBytes(), UTF_8)));
      }
    } finally {
      process.destroyForcibly();
    }
    if (process.exitValue() != 0) {
      throw new AssertionFailedError(
          "Process failed:%n%s"
              .formatted(new String(process.getErrorStream().readAllBytes(), UTF_8)));
    }
  }
}
