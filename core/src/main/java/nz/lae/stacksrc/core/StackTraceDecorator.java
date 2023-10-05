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
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Builds stack traces for {@link Throwable}s with code snippets applied.
 *
 * @see #decorate(Throwable)
 */
@AutoValue
public abstract class StackTraceDecorator {

  abstract Path searchPath();

  abstract int contextLineCount();

  abstract Predicate<StackTraceElement> filter();

  /** Builder for customizing a {@link StackTraceDecorator} */
  @AutoValue.Builder
  public abstract static class Builder {

    /** Sets the location to search for source code files, defaults to current working directory. */
    public abstract Builder searchPath(Path searchPath);

    /**
     * Sets the number of context lines to show around a given stack trace element, defaults to 2.
     *
     * <p>The following is an example with this value set to 2:
     *
     * <pre>
     *   	at example.HelloTest.hello(HelloTest.java:16)
     *
     * 	   14      @Test
     * 	   15      public void hello() {
     * 	-> 16          assertEquals("Hello!", greet());
     * 	   17          // a comment
     * 	   18      }
     * </pre>
     *
     * with 2 lines above and below the line pointed to by the stack trace element.
     */
    public abstract Builder contextLineCount(int contextLineCount);

    /**
     * Sets the additional filter on stack trace elements, if the filter returns false for a given
     * element, no attempt will be performed to decorate that element, defaults to no filtering.
     */
    public abstract Builder filter(Predicate<StackTraceElement> filter);

    public abstract StackTraceDecorator build();
  }

  /** Creates an instance with default configuration. */
  public static StackTraceDecorator create() {
    return builder().build();
  }

  /** Creates a builder with default configuration. */
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

  /**
   * Builds the stack trace for the given throwable with code snippets applied.
   *
   * @param e the throwable to decorate stack trace for
   * @return the stack trace of the throwable with code snippets applied
   */
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

    } catch (Exception ignored) {
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
      if (!filter().test(element)) {
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
