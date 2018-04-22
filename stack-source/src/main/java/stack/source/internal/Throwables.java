package stack.source.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import static java.lang.System.getProperty;
import static java.nio.file.Files.newBufferedReader;
import static java.util.Collections.newSetFromMap;

public final class Throwables {

    private Throwables() {
    }

    private static final String CAUSE_BY = "Caused by: ";
    private static final String SUPPRESSED = "Suppressed: ";
    private static final String LINE_SEPARATOR = getProperty("line.separator");

    public static void printStackTrace(
            Throwable e,
            Appendable out,
            boolean withSource
    ) throws IOException {
        printStackTraceWithSource(
                e, out, withSource, new HashMap<>(), new HashSet<>()
        );
    }

    private static void printStackTraceWithSource(
            Throwable e,
            Appendable out,
            boolean withSource,
            Map<String, Index> positiveCache,
            Set<String> negativeCache
    ) throws IOException {

        Set<Throwable> seen = newSetFromMap(new IdentityHashMap<>());

        seen.add(e);
        out.append(String.valueOf(e)).append(LINE_SEPARATOR);

        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement element : trace) {
            out.append("\tat ");
            printStackTraceWithSource(element, out, withSource, positiveCache, negativeCache);
            out.append(LINE_SEPARATOR);
        }

        for (Throwable sup : e.getSuppressed()) {
            printStackTraceWithSource(
                    sup, out, trace, SUPPRESSED, "\t", seen, withSource, positiveCache, negativeCache
            );
        }

        Throwable cause = e.getCause();
        if (cause != null) {
            printStackTraceWithSource(
                    cause, out, trace, CAUSE_BY, "", seen, withSource, positiveCache, negativeCache
            );
        }
    }

    private static void printStackTraceWithSource(
            Throwable e,
            Appendable out,
            StackTraceElement[] enclosingTrace,
            String caption,
            String prefix,
            Set<Throwable> seen,
            boolean withSource,
            Map<String, Index> positiveCache,
            Set<String> negativeCache
    ) throws IOException {

        if (!seen.add(e)) {
            out.append("\t[CIRCULAR REFERENCE:")
                    .append(String.valueOf(e))
                    .append("]")
                    .append(LINE_SEPARATOR);
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
            printStackTraceWithSource(trace[i], out, withSource, positiveCache, negativeCache);
            out.append(LINE_SEPARATOR);
        }

        if (framesInCommon != 0) {
            out.append(prefix);
            out.append("\t... ");
            out.append(String.valueOf(framesInCommon));
            out.append(" more");
            out.append(LINE_SEPARATOR);
        }

        for (Throwable se : e.getSuppressed()) {
            printStackTraceWithSource(
                    se, out, trace, SUPPRESSED, prefix + "\t", seen, withSource, positiveCache, negativeCache
            );
        }

        Throwable cause = e.getCause();
        if (cause != null) {
            printStackTraceWithSource(
                    cause, out, trace, CAUSE_BY, prefix, seen, withSource, positiveCache, negativeCache
            );
        }
    }

    private static void printStackTraceWithSource(
            StackTraceElement element,
            Appendable out,
            boolean withSource,
            Map<String, Index> positiveCache,
            Set<String> negativeCache
    ) throws IOException {

        out.append(element.toString());

        if (!withSource) {
            return;
        }

        String key = Index.relativePath(element);
        if (negativeCache.contains(key)) {
            return;
        }

        Index index = positiveCache.get(key);
        if (index == null) {
            Optional<Index> read = Index.read(element);
            if (!read.isPresent()) {
                negativeCache.add(key);
                return;
            }
            index = read.get();
            positiveCache.put(key, index);
        }

        Optional<IndexElement> indexElement = index.elements().stream()
                .filter(e -> e.startLineNum() <= element.getLineNumber() &&
                        e.endLineNum() >= element.getLineNumber())
                .findFirst();
        if (!indexElement.isPresent()) {
            return;
        }

        long startLineNum = indexElement.get().startLineNum();
        long endLineNum = indexElement.get().endLineNum();
        long startLineStartPos = indexElement.get().startLineStartPos();

        List<String> lines = new ArrayList<>((int) (endLineNum - startLineNum + 1)); // TODO
        try (BufferedReader in = newBufferedReader(index.source())) {
            if (in.skip(startLineStartPos) != startLineStartPos) {
                return;
            }
            long i = startLineNum;
            do {
                lines.add(in.readLine());
                i++;
            } while (i <= endLineNum);
        }

        int maxLineNumberDigits = String.valueOf(endLineNum).length();
        out.append(LINE_SEPARATOR);
        out.append(LINE_SEPARATOR);

        long lineNumber = startLineNum;
        for (String line : lines) {
            out.append("\t");
            out.append(lineNumber == element.getLineNumber()
                    ? "-> "
                    : "   ");

            String lineNumberStr = String.valueOf(lineNumber);
            for (int i = lineNumberStr.length(); i < maxLineNumberDigits; i++) {
                out.append(' ');
            }

            out.append(lineNumberStr);
            out.append("  ");
            out.append(line);
            out.append(LINE_SEPARATOR);

            lineNumber++;
        }
    }
}
