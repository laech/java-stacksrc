package stack.source.internal;

import com.google.auto.value.AutoValue;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
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

    List<String> lines(Path path) throws IOException {
        long lineCount = endLineNum() - startLineNum() + 1;
        try (BufferedReader in = newBufferedReader(path)) {
            if (in.skip(startLineStartPos()) != startLineStartPos()) {
                return emptyList();
            }
            return in.lines().limit(lineCount).collect(toList());
        }
    }

    static IndexRegion read(DataInput in) throws IOException {
        long startLineNum = in.readLong();
        long endLineNum = in.readLong();
        long startLineStartPos = in.readLong();
        return IndexRegion.create(startLineNum, endLineNum, startLineStartPos);
    }

    void write(DataOutput out) throws IOException {
        out.writeLong(startLineNum());
        out.writeLong(endLineNum());
        out.writeLong(startLineStartPos());
    }
}
