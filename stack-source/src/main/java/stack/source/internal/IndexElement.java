package stack.source.internal;

import com.google.auto.value.AutoValue;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Comparator;

@AutoValue
abstract class IndexElement implements Comparable<IndexElement> {

    private static final Comparator<IndexElement> comparator = Comparator
            .comparing(IndexElement::startLineNum)
            .thenComparing(IndexElement::endLineNum)
            .thenComparing(IndexElement::startLineStartPos);

    IndexElement() {
    }

    static IndexElement create(
            long startLineNum,
            long endLineNum,
            long startLineStartPos
    ) {
        return new AutoValue_IndexElement(
                startLineNum, endLineNum, startLineStartPos
        );
    }

    abstract long startLineNum();

    abstract long endLineNum();

    abstract long startLineStartPos();

    @Override
    public int compareTo(IndexElement o) {
        return comparator.compare(this, o);
    }

    static IndexElement read(DataInput in) throws IOException {
        long startLineNum = in.readLong();
        long endLineNum = in.readLong();
        long startLineStartPos = in.readLong();
        return IndexElement.create(startLineNum, endLineNum, startLineStartPos);
    }

    void write(DataOutput out) throws IOException {
        out.writeLong(startLineNum());
        out.writeLong(endLineNum());
        out.writeLong(startLineStartPos());
    }
}
