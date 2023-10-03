package nz.lae.stacksrc.core;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import com.google.auto.value.AutoValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@AutoValue
public abstract class StackTraceDecorator {

  abstract Path searchPath();

  abstract int contextLineCount();

  abstract Predicate<StackTraceElement> filter();

  @AutoValue.Builder
  public abstract static class Builder {

    /** Location to search for source code. Defaults to current working directory. */
    public abstract Builder searchPath(Path searchPath);

    /** Number of context lines to show for a given stack trace element. Defaults to 2. */
    public abstract Builder contextLineCount(int contextLineCount);

    /**
     * Additional filter on stack trace elements, if returned value is false for a given element, no
     * attempt will be performed to decorate that element. Defaults to no filtering.
     */
    public abstract Builder filter(Predicate<StackTraceElement> filter);

    public abstract StackTraceDecorator build();
  }

  /** Creates an instance with default configuration. */
  public static StackTraceDecorator create() {
    return builder().build();
  }

  public static Builder builder() {
    return new AutoValue_StackTraceDecorator.Builder()
        .searchPath(Paths.get(""))
        .contextLineCount(2)
        .filter(__ -> true);
  }

  private volatile Map<String, List<Path>> cachedFiles;

  private Map<String, List<Path>> cachedFiles() throws IOException {
    if (cachedFiles == null) {
      cachedFiles = FileCollector.collect(searchPath());
    }
    return cachedFiles;
  }

  /** Returns the stack trace of the throwable with code snippets applied. */
  public String decorate(Throwable e) {
    requireNonNull(e);

    var output = getStackTraceAsString(e);
    try {

      var snippets = new HashSet<String>();
      for (var element : e.getStackTrace()) {
        if (!filter().test(element)) {
          continue;
        }

        var snippet = decorate(element);
        if (snippet.isEmpty() || !snippets.add(snippet.get())) {
          // Don't print the same snippet multiple times,
          // multiple lambda on one line can create this situation
          continue;
        }

        var line = element.toString();
        var replacement = String.format("%s%n%n%s%n%n", line, snippet.get());
        output = output.replace(line, replacement);
      }

    } catch (Exception ignored) {
    }
    return output;
  }

  private Optional<String> decorate(StackTraceElement element) {
    return findFile(element)
        .map(
            path -> {
              try {
                return readContextLines(element, path);
              } catch (IOException e) {
                return null;
              }
            })
        .filter(it -> !it.isEmpty())
        .map(this::removeBlankLinesFromStart)
        .map(this::removeBlankLinesFromEnd)
        .map(it -> buildSnippet(it, element));
  }

  private Optional<Path> findFile(StackTraceElement element) {
    return Optional.of(element)
        .filter(it -> it.getLineNumber() > 0)
        .filter(it -> it.getFileName() != null)
        .filter(it -> !it.getMethodName().startsWith("access$"))
        .map(
            it -> {
              try {
                return Class.forName(it.getClassName(), false, getClass().getClassLoader());
              } catch (ClassNotFoundException e) {
                return null;
              }
            })
        .map(Class::getProtectionDomain)
        .map(ProtectionDomain::getCodeSource)
        .map(CodeSource::getLocation)
        .filter(url -> "file".equalsIgnoreCase(url.getProtocol()))
        .map(
            url -> {
              try {
                return Paths.get(url.toURI());
              } catch (URISyntaxException e) {
                return null;
              }
            })
        .filter(Files::isDirectory)
        .flatMap(
            __ -> {
              try {
                var candidates = cachedFiles().getOrDefault(element.getFileName(), List.of());
                if (element.getFileName().endsWith(".java")) {
                  var suffix = withPackagePath(element);
                  candidates =
                      candidates.stream().filter(path -> path.endsWith(suffix)).collect(toList());
                }
                return Optional.ofNullable(candidates.size() == 1 ? candidates.get(0) : null);
              } catch (IOException ignored) {
                return Optional.empty();
              }
            });
  }

  private Path withPackagePath(StackTraceElement element) {
    var fileName = requireNonNull(element.getFileName());
    var className = element.getClassName();
    var i = className.lastIndexOf(".");
    var parent = i < 0 ? "" : className.substring(0, i).replace('.', '/');
    return Paths.get(parent).resolve(fileName);
  }

  private NavigableMap<Integer, String> readContextLines(StackTraceElement elem, Path path)
      throws IOException {

    var startLineNum = Math.max(1, elem.getLineNumber() - contextLineCount());
    try (var stream = Files.lines(path)) {

      var lines =
          stream
              .limit(elem.getLineNumber() + contextLineCount())
              .skip(startLineNum - 1)
              .collect(toList());

      return IntStream.range(0, lines.size())
          .boxed()
          .reduce(
              new TreeMap<>(),
              (acc, i) -> {
                acc.put(i + startLineNum, lines.get(i));
                return acc;
              },
              (a, b) -> b);
    }
  }

  private NavigableMap<Integer, String> removeBlankLinesFromStart(
      NavigableMap<Integer, String> lines) {
    IntStream.rangeClosed(lines.firstKey(), lines.lastKey())
        .takeWhile(i -> lines.get(i).isBlank())
        .forEach(lines::remove);
    return lines;
  }

  private NavigableMap<Integer, String> removeBlankLinesFromEnd(
      NavigableMap<Integer, String> lines) {
    IntStream.iterate(lines.lastKey(), i -> i >= lines.firstKey(), i -> i - 1)
        .takeWhile(i -> lines.get(i).isBlank())
        .forEach(lines::remove);
    return lines;
  }

  private static String buildSnippet(NavigableMap<Integer, String> lines, StackTraceElement elem) {
    var maxLineNumWidth = String.valueOf(lines.lastKey()).length();
    return lines.entrySet().stream()
        .map(
            entry -> {
              var lineNum = entry.getKey();
              var isTargetLine = lineNum == elem.getLineNumber();
              var line = entry.getValue();
              var lineNumStr = format("%" + maxLineNumWidth + "d", lineNum);
              return format(
                  "\t%s %s%s",
                  isTargetLine ? "->" : "  ", lineNumStr, line.isEmpty() ? "" : "  " + line);
            })
        .collect(joining(lineSeparator()));
  }

  private static String getStackTraceAsString(Throwable e) {
    var stringWriter = new StringWriter();
    var printWriter = new PrintWriter(stringWriter);
    e.printStackTrace(printWriter);
    printWriter.flush();
    return stringWriter.toString();
  }
}
