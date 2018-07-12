package stack.source.internal;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Objects;
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

        StackTraceElement[] elements = throwable.getStackTrace();

        // We only want to decorate a single (hopefully the most useful)
        // stack trace element, otherwise the decorated output looks messy,
        // which defeats the purpose.
        Decoration decor = null;

        for (int i = elements.length - 1; i >= 0; i--) {
            StackTraceElement element = elements[i];

            // If we have found one already, ignore nested calls to other files
            if (decor != null && !Objects.equals(decor.element.getFileName(), element.getFileName())) {
                continue;
            }

            Optional<Entry<Index, IndexRegion>> op = read(element);
            if (op.isPresent()) {

                // If a nested element has the same region as an outer element,
                // pick the nested one as it has a more specific line number
                if (decor == null || decor.region.equals(op.get().getValue())) {
                    decor = new Decoration(element, op.get().getKey(), op.get().getValue());
                }
            }
        }

        return decor != null
                ? decor.decorate(throwable)
                : getStackTraceAsString(throwable);
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

        String decorate(Throwable e) {
            String line = element.toString();
            return getStackTraceAsString(e).replace(line, line
                    + lineSeparator()
                    + lineSeparator()
                    + toString()
                    + lineSeparator());
        }
    }
}
