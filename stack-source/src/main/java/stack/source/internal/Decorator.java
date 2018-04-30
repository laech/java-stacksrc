package stack.source.internal;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Logger.getLogger;
import static stack.source.internal.Index.relativePath;
import static stack.source.internal.Throwables.getStackTraceAsString;

public final class Decorator {

    private final Throwable throwable;
    private final Map<String, Optional<Index>> indexes = new HashMap<>();
    private final Map<StackTraceElement, String> snippets = new HashMap<>();
    private final Set<Entry<Index, IndexRegion>> printedRegions = new HashSet<>();

    public Decorator(Throwable throwable) {
        this.throwable = requireNonNull(throwable);
    }

    public void printSafely(PrintStream out) {
        printSafely(new PrintWriter(out));
    }

    public void printSafely(PrintWriter out) {
        try {
            out.println(print());
        } catch (Throwable e) {
            throwable.printStackTrace(out);
            getLogger(getClass().getName()).warning(() ->
                    "Failed to decorate " + getStackTraceAsString(e));
        }
    }

    public String print() throws IOException {

        StackTraceElement[] stacks = throwable.getStackTrace();

        int i = 0;
        for (; i < stacks.length && snippets.size() < 1; i++) {
            read(stacks[i]);
        }

        for (int j = stacks.length - 1; j >= i && snippets.size() < 3; j--) {
            read(stacks[j]);
        }

        return snippets.entrySet().stream().reduce(
                getStackTraceAsString(throwable),
                (str, entry) -> {
                    String line = entry.getKey().toString();
                    return str.replace(line, line + lineSeparator() + lineSeparator() +
                            entry.getValue() + lineSeparator());
                },
                (a, b) -> {
                    throw new UnsupportedOperationException();
                });
    }

    private void read(StackTraceElement stack) throws IOException {
        Optional<Index> index = findIndex(stack);
        if (!index.isPresent()) {
            return;
        }

        Optional<IndexRegion> region = findRegion(stack, index.get());
        if (!region.isPresent() ||
                !printedRegions.add(new SimpleEntry<>(index.get(), region.get()))) {
            return;
        }

        snippets.put(stack, printRegion(stack, index.get(), region.get()));
    }

    private Optional<Index> findIndex(StackTraceElement stack) {
        return indexes.computeIfAbsent(relativePath(stack), key -> {
            try {
                return Index.read(stack);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private Optional<IndexRegion> findRegion(StackTraceElement stack, Index index) {
        return index.regions().stream()
                .filter(e -> stack.getLineNumber() >= e.startLineNum())
                .filter(e -> stack.getLineNumber() <= e.endLineNum())
                .sorted(comparing(IndexRegion::lineCount))
                .reduce((a, b) -> a.lineCount() <= 1 ? b : a);
    }

    private String printRegion(StackTraceElement stack, Index index, IndexRegion region)
            throws IOException {
        StringBuilder out = new StringBuilder();
        int width = String.valueOf(region.endLineNum()).length();
        long lineNumber = region.startLineNum();
        for (String line : region.lines(index.source())) {
            out.append("\t");
            out.append(lineNumber == stack.getLineNumber()
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

}
