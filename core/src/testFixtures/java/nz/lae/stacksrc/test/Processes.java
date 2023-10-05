package nz.lae.stacksrc.test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import org.opentest4j.AssertionFailedError;

public class Processes {
  private Processes() {}

  public static void run(Path directory, String... command)
      throws IOException, InterruptedException, ExecutionException {
    var builder = new ProcessBuilder(command).directory(directory.toFile());
    var process = builder.start();
    var executor = Executors.newFixedThreadPool(2);
    var stdout = executor.submit(() -> new String(process.getInputStream().readAllBytes(), UTF_8));
    var stderr = executor.submit(() -> new String(process.getErrorStream().readAllBytes(), UTF_8));
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
                .formatted(stdout.get(), stderr.get()));
      }
    } finally {
      process.destroyForcibly();
    }
    if (process.exitValue() != 0) {
      throw new AssertionFailedError("Process failed:%n%s".formatted(stderr.get()));
    }
  }
}
