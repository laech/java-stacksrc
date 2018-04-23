package stack.source.internal;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.util.Collections.newSetFromMap;
import static java.util.Objects.requireNonNull;

public final class Decorator {

    private static final String CAUSE_BY = "Caused by: ";
    private static final String SUPPRESSED = "Suppressed: ";
    private static final String LINE_SEPARATOR = getProperty("line.separator");

    private final Throwable throwable;
    private final boolean decorate;
    private final Map<String, Index> positiveCache = new HashMap<>();
    private final Set<String> negativeCache = new HashSet<>();
    private final Set<Throwable> seen = newSetFromMap(new IdentityHashMap<>());

    public Decorator(Throwable throwable) {
        this(throwable, true);
    }

    Decorator(Throwable throwable, boolean decorate) {
        this.throwable = requireNonNull(throwable);
        this.decorate = decorate;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        try {
            print(builder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return builder.toString();
    }

    private void print(Appendable out) throws IOException {
        seen.add(throwable);
        out.append(String.valueOf(throwable));
        out.append(LINE_SEPARATOR);

        StackTraceElement[] trace = throwable.getStackTrace();
        for (StackTraceElement element : trace) {
            out.append("\tat ");
            print(element, out);
            out.append(LINE_SEPARATOR);
        }

        for (Throwable sup : throwable.getSuppressed()) {
            print(sup, out, trace, SUPPRESSED, "\t");
        }

        Throwable cause = throwable.getCause();
        if (cause != null) {
            print(cause, out, trace, CAUSE_BY, "");
        }
    }

    private void print(
            Throwable e,
            Appendable out,
            StackTraceElement[] enclosingTrace,
            String caption,
            String prefix
    ) throws IOException {

        if (!seen.add(e)) {
            printCircular(e, out);
            return;
        }

        StackTraceElement[] trace = e.getStackTrace();
        int m = trace.length - 1;
        int n = enclosingTrace.length - 1;
        while (m >= 0 && n >= 0 && trace[m].equals(enclosingTrace[n])) {
            m--;
            n--;
        }
        int framesInCommon = trace.length - 1 - m;

        out.append(prefix);
        out.append(caption);
        out.append(String.valueOf(e));
        out.append(LINE_SEPARATOR);

        for (int i = 0; i <= m; i++) {
            out.append(prefix).append("\tat ");
            print(trace[i], out);
            out.append(LINE_SEPARATOR);
        }

        if (framesInCommon != 0) {
            printCommonFrames(out, prefix, framesInCommon);
        }

        for (Throwable se : e.getSuppressed()) {
            print(se, out, trace, SUPPRESSED, prefix + "\t");
        }

        Throwable cause = e.getCause();
        if (cause != null) {
            print(cause, out, trace, CAUSE_BY, prefix);
        }
    }

    private void printCircular(Throwable e, Appendable out) throws IOException {
        out.append("\t[CIRCULAR REFERENCE:");
        out.append(String.valueOf(e));
        out.append("]");
        out.append(LINE_SEPARATOR);
    }

    private void printCommonFrames(Appendable out, String prefix, int count)
            throws IOException {
        out.append(prefix);
        out.append("\t... ");
        out.append(String.valueOf(count));
        out.append(" more");
        out.append(LINE_SEPARATOR);
    }

    private void print(StackTraceElement element, Appendable out)
            throws IOException {

        out.append(element.toString());

        if (!decorate) {
            return;
        }

        Index index = findIndex(element);
        if (index == null) {
            return;
        }

        Optional<IndexRegion> region = index.regions().stream()
                .filter(e -> e.startLineNum() <= element.getLineNumber() &&
                        e.endLineNum() >= element.getLineNumber())
                .findFirst();
        if (region.isPresent()) {
            print(element, out, index, region.get());
        }
    }

    private void print(
            StackTraceElement element,
            Appendable out,
            Index index,
            IndexRegion region
    ) throws IOException {

        List<String> lines = region.lines(index.source());
        if (lines.isEmpty()) {
            return;
        }

        int width = String.valueOf(region.endLineNum()).length();
        out.append(LINE_SEPARATOR);
        out.append(LINE_SEPARATOR);

        long lineNumber = region.startLineNum();
        for (String line : lines) {
            out.append("\t");
            out.append(lineNumber == element.getLineNumber()
                    ? "-> "
                    : "   "
            );
            out.append(format("%" + width + "d", lineNumber));
            out.append("  ");
            out.append(line);
            out.append(LINE_SEPARATOR);
            lineNumber++;
        }
    }

    private Index findIndex(StackTraceElement element) throws IOException {
        String key = Index.relativePath(element);
        if (negativeCache.contains(key)) {
            return null;
        }

        Index index = positiveCache.get(key);
        if (index == null) {
            Optional<Index> read = Index.read(element);
            if (!read.isPresent()) {
                negativeCache.add(key);
                return null;
            }
            index = read.get();
            positiveCache.put(key, index);
        }
        return index;
    }
}
