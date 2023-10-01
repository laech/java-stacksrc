package nz.lae.stacksrc.it;

import static java.util.concurrent.TimeUnit.MINUTES;

import java.io.IOException;
import java.nio.file.Path;
import org.opentest4j.AssertionFailedError;

class Processes {
  private Processes() {}

  static void run(Path directory, String... command) throws IOException, InterruptedException {
    var builder = new ProcessBuilder(command).directory(directory.toFile()).inheritIO();
    var process = builder.start();
    try {
      if (!process.waitFor(2, MINUTES)) {
        throw new AssertionFailedError("Timed out.");
      }
    } finally {
      process.destroyForcibly();
    }
    if (process.exitValue() != 0) {
      throw new AssertionFailedError("Process failed, check the log for error.");
    }
  }
}
