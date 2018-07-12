package stack.source.internal;

import com.google.auto.value.AutoValue;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.Files.newBufferedReader;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@AutoValue
abstract class IndexRegion {

    IndexRegion() {
    }

    static IndexRegion create(
            long startLineNum,
            long endLineNum,
            long startLineStartPos
    ) {
        long lineCount = endLineNum - startLineNum;
        if (lineCount < 0) {
            throw new IllegalArgumentException(
                    "startLineNum=" + startLineNum
                            + ", endLineNum=" + endLineNum);
        }
        return new AutoValue_IndexRegion(
                startLineNum, endLineNum, startLineStartPos
        );
    }

    abstract long startLineNum();

    abstract long endLineNum();

    abstract long startLineStartPos();

    long lineCount() {
        return endLineNum() - startLineNum() + 1;
    }

    List<String> lines(Path path) {
        long lineCount = endLineNum() - startLineNum() + 1;
        try (BufferedReader in = newBufferedReader(path)) {
            if (in.skip(startLineStartPos()) != startLineStartPos()) {
                return emptyList();
            }
            return in.lines().limit(lineCount).collect(toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static IndexRegion read(DataInput in) {
        try {
            long startLineNum = in.readLong();
            long endLineNum = in.readLong();
            long startLineStartPos = in.readLong();
            return IndexRegion.create(startLineNum, endLineNum, startLineStartPos);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    void write(DataOutput out) {
        try {
            out.writeLong(startLineNum());
            out.writeLong(endLineNum());
            out.writeLong(startLineStartPos());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
