package nz.lae.stacksrc.core;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.IntStream;

public final class StackTraceDecorator {

  private final Path searchPath;
  private final int contextLineCount;

  /**
   * Creates a decorator with default config, where search path is set to the current working
   * directory, and context line count is 2.
   */
  public StackTraceDecorator() {
    this(Paths.get(""), 2);
  }

  /**
   * @param searchPath where to search for source code
   * @param contextLineCount number of context lines to show for a given stack trace element
   */
  public StackTraceDecorator(Path searchPath, int contextLineCount) {
    if (contextLineCount < 1) {
      throw new IllegalArgumentException("Invalid contextLineCount: " + contextLineCount);
    }
    this.searchPath = requireNonNull(searchPath, "searchPath");
    this.contextLineCount = contextLineCount;
  }

  /** Returns the stack trace of the throwable with code snippets applied. */
  public String decorateStackTrace(Throwable e) {
    requireNonNull(e);

    var output = getStackTraceAsString(e);
    try {

      var snippets = new HashSet<String>();
      for (var elem : e.getStackTrace()) {

        var snippet = decorate(elem);
        if (snippet.isEmpty() || !snippets.add(snippet.get())) {
          // Don't print the same snippet multiple times,
          // multiple lambda on one line can create this situation
          continue;
        }

        var line = elem.toString();
        var replacement = String.format("%s%n%n%s%n%n", line, snippet.get());
        output = output.replace(line, replacement);
      }

    } catch (IOException | URISyntaxException | ClassNotFoundException ignored) {
    }
    return output;
  }

  private Optional<String> decorate(StackTraceElement elem)
      throws ClassNotFoundException, URISyntaxException, IOException {

    if (elem.getLineNumber() < 1
        || elem.getFileName() == null
        || elem.getMethodName().startsWith("access$")) { // Ignore class entry
      return Optional.empty();
    }

    var clazz =
        Class.forName(elem.getClassName(), false, StackTraceDecorator.class.getClassLoader());
    var source = clazz.getProtectionDomain().getCodeSource();
    if (source == null) {
      return Optional.empty();
    }

    var location = source.getLocation();
    if (location == null || !"file".equalsIgnoreCase(location.getProtocol())) {
      return Optional.empty();
    }

    var dir = Paths.get(location.toURI());
    if (!Files.isDirectory(dir)) {
      return Optional.empty();
    }

    var path = findFile(elem.getFileName());
    if (path.isEmpty()) {
      return Optional.empty();
    }

    var contextLines = readContextLines(elem, path.get());
    removeBlankLinesFromStart(contextLines);
    removeBlankLinesFromEnd(contextLines);
    return Optional.of(buildSnippet(contextLines, elem));
  }

  private Optional<Path> findFile(String fileName) throws IOException {
    // TODO add some caching
    try (var stream =
        Files.find(
            searchPath,
            Integer.MAX_VALUE,
            (path, attrs) ->
                attrs.isRegularFile() && path.getFileName().toString().equals(fileName))) {

      var paths = stream.limit(2).collect(toList());
      return Optional.ofNullable(paths.size() == 1 ? paths.get(0) : null);
    }
  }

  private NavigableMap<Integer, String> readContextLines(StackTraceElement elem, Path path)
      throws IOException {

    var startLineNum = Math.max(1, elem.getLineNumber() - contextLineCount);
    try (var stream = Files.lines(path)) {

      var lines =
          stream
              .limit(elem.getLineNumber() + contextLineCount)
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

  private static void removeBlankLinesFromStart(NavigableMap<Integer, String> lines) {
    IntStream.rangeClosed(lines.firstKey(), lines.lastKey())
        .takeWhile(i -> lines.get(i).isBlank())
        .forEach(lines::remove);
  }

  private static void removeBlankLinesFromEnd(NavigableMap<Integer, String> lines) {
    IntStream.iterate(lines.lastKey(), i -> i >= lines.firstKey(), i -> i - 1)
        .takeWhile(i -> lines.get(i).isBlank())
        .forEach(lines::remove);
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
