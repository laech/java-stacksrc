package nz.lae.stacksrc.core;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.nio.file.Files.isDirectory;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static nz.lae.stacksrc.core.Throwables.getStackTraceAsString;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.IntStream;

public final class Decorator {
  private Decorator() {}

  private static final int CONTEXT_LINE_COUNT = 2;

  public static String print(Throwable throwable) {
    var output = getStackTraceAsString(throwable);
    try {

      var snippets = new HashSet<String>();
      for (var elem : throwable.getStackTrace()) {

        var snippet = getSourceSnippet(elem);
        if (snippet.isEmpty() || !snippets.add(snippet.get())) {
          // Don't print the same snippet multiple times,
          // multiple lambda on one line can create this situation
          continue;
        }

        var line = elem.toString();
        var replacement =
            line + lineSeparator() + lineSeparator() + snippet.get() + lineSeparator();
        output = output.replace(line, replacement);
      }

    } catch (IOException | URISyntaxException | ClassNotFoundException ignored) {
    }
    return output;
  }

  private static Optional<String> getSourceSnippet(StackTraceElement elem)
      throws ClassNotFoundException, URISyntaxException, IOException {

    if (elem.getLineNumber() < 1
        || elem.getFileName() == null
        || elem.getMethodName().startsWith("access$")) { // Ignore class entry
      return Optional.empty();
    }

    var clazz = Class.forName(elem.getClassName(), false, Decorator.class.getClassLoader());
    var source = clazz.getProtectionDomain().getCodeSource();
    if (source == null) {
      return Optional.empty();
    }

    var location = source.getLocation();
    if (location == null || !"file".equalsIgnoreCase(location.getProtocol())) {
      return Optional.empty();
    }

    var dir = Paths.get(location.toURI());
    if (!isDirectory(dir)) {
      return Optional.empty();
    }

    var path = findFile(elem.getFileName(), clazz);
    if (path.isEmpty()) {
      return Optional.empty();
    }

    var contextLines = readContextLines(elem, path.get());
    removeBlankLinesFromStart(contextLines);
    removeBlankLinesFromEnd(contextLines);
    return Optional.of(buildSnippet(contextLines, elem));
  }

  private static Optional<Path> findFile(String fileName, Class<?> clazz) throws IOException {
    var suffix =
        Optional.ofNullable(clazz.getPackage())
            .map(pkg -> pkg.getName().split("\\."))
            .map(parts -> Paths.get("", parts))
            .orElseGet(() -> Paths.get(""))
            .resolve(fileName);

    try (var paths =
        Files.find(
            Paths.get(""),
            Integer.MAX_VALUE,
            (p, attrs) -> attrs.isRegularFile() && p.endsWith(suffix))) {
      return paths.findFirst();
    }
  }

  private static NavigableMap<Integer, String> readContextLines(StackTraceElement elem, Path path)
      throws IOException {

    var startLineNum = Math.max(1, elem.getLineNumber() - CONTEXT_LINE_COUNT);
    try (var stream = Files.lines(path)) {

      var lines =
          stream
              .limit(elem.getLineNumber() + CONTEXT_LINE_COUNT)
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
              return format("\t%s %s  %s", isTargetLine ? "->" : "  ", lineNumStr, line);
            })
        .collect(joining(lineSeparator(), "", lineSeparator()));
  }
}
