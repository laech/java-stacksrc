package stack.source.internal;

import java.io.*;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import static java.lang.System.getProperty;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.newSetFromMap;

public final class Throwables {

    private Throwables() {
    }

    private static final String CAUSE_BY = "Caused by: ";
    private static final String SUPPRESSED = "Suppressed: ";
    private static final String LINE_SEPARATOR = getProperty("line.separator");

    public static void printStackTraceWithSource(
            Throwable e,
            Appendable out) throws IOException {

        Set<Throwable> seen =
                newSetFromMap(new IdentityHashMap<Throwable, Boolean>());

        seen.add(e);
        out.append(String.valueOf(e)).append(LINE_SEPARATOR);

        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement element : trace) {
            out.append("\tat ");
            printStackTraceWithSource(element, out);
            out.append(LINE_SEPARATOR);
        }

        for (Throwable sup : e.getSuppressed()) {
            printStackTraceWithSource(
                    sup, out, trace, SUPPRESSED, "\t", seen);
        }

        Throwable cause = e.getCause();
        if (cause != null) {
            printStackTraceWithSource(
                    cause, out, trace, CAUSE_BY, "", seen);
        }
    }

    private static void printStackTraceWithSource(
            Throwable e,
            Appendable out,
            StackTraceElement[] enclosingTrace,
            String caption,
            String prefix,
            Set<Throwable> seen) throws IOException {

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
            printStackTraceWithSource(trace[i], out);
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
                    se, out, trace, SUPPRESSED, prefix + "\t", seen);
        }

        Throwable cause = e.getCause();
        if (cause != null) {
            printStackTraceWithSource(
                    cause, out, trace, CAUSE_BY, prefix, seen);
        }
    }

    private static void printStackTraceWithSource(
            StackTraceElement element,
            Appendable out
    ) throws IOException {

        out.append(element.toString());

        String resource = getPackageName(element).replace('.', '/') + "/" + element.getFileName();
        if (!resource.startsWith("/")) {
            resource = "/" + resource;
        }

        long startLineNumber;
        long endLineNumber;
        long startLinePosition;
        try (InputStream in = element.getClass().getResourceAsStream(resource + ".index")) {
            if (in == null) {
                return;
            }
            DataInputStream data = new DataInputStream(new BufferedInputStream(in));
            while (true) {
                try {
                    startLineNumber = data.readLong();
                    endLineNumber = data.readLong();
                    startLinePosition = data.readLong();
                    if (startLineNumber <= element.getLineNumber() &&
                            endLineNumber >= element.getLineNumber()) {
                        break;
                    }
                } catch (EOFException e) {
                    return;
                }
            }
        }

        List<String> lines = new ArrayList<>((int) (endLineNumber - startLineNumber + 1)); // TODO
        try (InputStream in = element.getClass().getResourceAsStream(resource)) {
            if (in == null) {
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, UTF_8));
            if (reader.skip(startLinePosition) != startLinePosition) {
                return;
            }
            long i = startLineNumber;
            do {
                lines.add(reader.readLine());
                i++;
            } while (i <= endLineNumber);
        }

        int maxLineNumberDigits = String.valueOf(endLineNumber).length();
        out.append(LINE_SEPARATOR);
        out.append(LINE_SEPARATOR);

        long lineNumber = startLineNumber;
        for (String line : lines) {
            out.append("\t\t");
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

    private static String getPackageName(StackTraceElement element) {
        String fullyQualifiedClassName = element.getClassName();
        int packageNameEnd = fullyQualifiedClassName.lastIndexOf('.');
        if (packageNameEnd > 0) {
            return fullyQualifiedClassName.substring(0, packageNameEnd);
        } else {
            return "";
        }
    }
}
