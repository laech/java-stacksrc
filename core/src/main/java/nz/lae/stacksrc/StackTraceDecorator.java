package nz.lae.stacksrc;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.IntStream;

final class StackTraceDecorator {
  private StackTraceDecorator() {}

  private static final StackTraceDecorator instance = new StackTraceDecorator();

  public static StackTraceDecorator get() {
    return instance;
  }

  private static final int CONTEXT_LINE_COUNT = 2;

  private volatile Map<String, List<Path>> cachedFiles;

  private Map<String, List<Path>> cachedFiles() throws IOException {
    if (cachedFiles == null) {
      cachedFiles = FileCollector.collect(Paths.get(""));
    }
    return cachedFiles;
  }

  public String decorate(Throwable e) {
    requireNonNull(e);

    var stackTrace = getStackTraceAsString(e);
    try {

      var alreadySeenElements = new HashSet<StackTraceElement>();
      var alreadySeenSnippets = new HashSet<List<String>>();
      stackTrace = decorate(e, stackTrace, 1, alreadySeenElements, alreadySeenSnippets);

      var cause = e.getCause();
      if (cause != null) {
        stackTrace = decorate(cause, stackTrace, 1, alreadySeenElements, alreadySeenSnippets);
      }

      for (var suppressed : e.getSuppressed()) {
        stackTrace = decorate(suppressed, stackTrace, 2, alreadySeenElements, alreadySeenSnippets);
      }

    } catch (Exception sup) {
      e.addSuppressed(sup);
    }
    return stackTrace;
  }

  private String decorate(
      Throwable e,
      String stackTrace,
      int indentLevel,
      Set<StackTraceElement> alreadySeenElements,
      Set<List<String>> alreadySeenSnippets) {

    for (var element : e.getStackTrace()) {
      if (!alreadySeenElements.add(element)) {
        continue;
      }

      var snippet = decorate(element);
      if (snippet.isEmpty() || !alreadySeenSnippets.add(snippet.get())) {
        // Don't print the same snippet multiple times,
        // multiple lambda on one line can create this situation
        continue;
      }

      var line = element.toString();
      var indent = "\t".repeat(indentLevel);
      var replacement =
          String.format(
              "%s%n%n%s%n%n",
              line, snippet.get().stream().collect(joining(lineSeparator() + indent, indent, "")));
      stackTrace = stackTrace.replace(line, replacement);
    }
    return stackTrace;
  }

  private Optional<List<String>> decorate(StackTraceElement element) {
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
        .flatMap(
            __ -> {
              try {
                var suffix = withPackagePath(element);
                var potentialMatches = cachedFiles().getOrDefault(element.getFileName(), List.of());
                var exactMatch =
                    potentialMatches.stream()
                        .filter(path -> path.endsWith(suffix))
                        .collect(toList())
                        .stream()
                        .findAny();

                if (exactMatch.isPresent() || element.getFileName().endsWith(".java")) {
                  return exactMatch;
                }

                return Optional.ofNullable(
                    potentialMatches.size() == 1 ? potentialMatches.get(0) : null);

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

  private static List<String> buildSnippet(
      NavigableMap<Integer, String> lines, StackTraceElement elem) {
    var maxLineNumWidth = String.valueOf(lines.lastKey()).length();
    return lines.entrySet().stream()
        .map(
            entry -> {
              var lineNum = entry.getKey();
              var isTarget = lineNum == elem.getLineNumber();
              var line = entry.getValue();
              var lineNumStr = format("%" + maxLineNumWidth + "d", lineNum);
              return format(
                  "%s %s%s", isTarget ? "->" : "  ", lineNumStr, line.isEmpty() ? "" : "  " + line);
            })
        .collect(toList());
  }

  private static String getStackTraceAsString(Throwable e) {
    var stringWriter = new StringWriter();
    var printWriter = new PrintWriter(stringWriter);
    e.printStackTrace(printWriter);
    printWriter.flush();
    return stringWriter.toString();
  }
}
