package stack.source.internal;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Logger.getLogger;
import static stack.source.internal.Throwables.getStackTraceAsString;

public final class Decorator {

    private Decorator() {
    }

    public static void printSafely(Throwable throwable, PrintStream out) {
        printSafely(throwable, new PrintWriter(out));
    }

    public static void printSafely(Throwable throwable, PrintWriter out) {
        try {
            out.println(print(throwable));
        } catch (Throwable e) {
            throwable.printStackTrace(out);
            getLogger(Decorator.class.getName()).warning(() ->
                    "Failed to decorate " + getStackTraceAsString(e));
        }
    }

    public static String print(Throwable throwable) {

        Map<String, Map<IndexRegion, Decoration>> decorations = new HashMap<>();

        for (StackTraceElement element : throwable.getStackTrace()) {
            if (element.getFileName() == null) {
                continue;
            }
            read(element).ifPresent(entry -> {
                Index index = entry.getKey();
                IndexRegion region = entry.getValue();
                decorations
                        .computeIfAbsent(element.getFileName(), __ -> new HashMap<>())
                        .computeIfAbsent(region, r -> new Decoration(element, index, r));
            });
        }

        String stackTrace = getStackTraceAsString(throwable);
        for (Map<IndexRegion, Decoration> values : decorations.values()) {
            for (Decoration decoration : values.values()) {
                stackTrace = decoration.decorate(stackTrace);
            }
        }
        return stackTrace;
    }

    private static Optional<Entry<Index, IndexRegion>> read(StackTraceElement stack) {
        return Index.read(stack)
                .flatMap(index -> findRegion(stack, index)
                        .map(region -> new SimpleEntry<>(index, region)));
    }

    private static Optional<IndexRegion> findRegion(StackTraceElement stack, Index index) {
        return index.regions().stream()
                .filter(e -> stack.getLineNumber() >= e.startLineNum())
                .filter(e -> stack.getLineNumber() <= e.endLineNum())
                .sorted(comparing(IndexRegion::lineCount))
                .reduce((a, b) -> a.lineCount() <= 2 && b.lineCount() <= 10 ? b : a);
    }

    private static final class Decoration {
        final StackTraceElement element;
        final Index index;
        final IndexRegion region;

        Decoration(StackTraceElement element, Index index, IndexRegion region) {
            this.element = requireNonNull(element);
            this.index = requireNonNull(index);
            this.region = requireNonNull(region);
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder();
            int width = String.valueOf(region.endLineNum()).length();
            long lineNumber = region.startLineNum();
            for (String line : region.lines(index.source())) {
                out.append("\t");
                out.append(lineNumber == element.getLineNumber()
                        ? "-> "
                        : "   "
                );
                out.append(format("%" + width + "d", lineNumber));
                out.append("  ");
                out.append(line);
                out.append(lineSeparator());
                lineNumber++;
            }
            return out.toString();
        }

        String decorate(String stackTrace) {
            String line = element.toString();
            return stackTrace.replace(line, line
                    + lineSeparator()
                    + lineSeparator()
                    + toString()
                    + lineSeparator());
        }
    }
}
