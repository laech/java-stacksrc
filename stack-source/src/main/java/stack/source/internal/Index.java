package stack.source.internal;

import com.google.auto.value.AutoValue;
import com.sun.source.tree.CompilationUnitTree;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.TreeSet;

import static java.util.Collections.unmodifiableNavigableSet;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

@AutoValue
abstract class Index {

    private static final byte VERSION = 1;

    Index() {
    }

    static Index create(Path source, NavigableSet<IndexRegion> regions) {
        return new AutoValue_Index(
                source.toAbsolutePath(),
                unmodifiableNavigableSet(regions)
        );
    }

    abstract Path source();

    abstract NavigableSet<IndexRegion> regions();

    private static String relativePath(String pkgName, String fileName) {
        return String.join("/", "stack-source", pkgName, fileName + ".index");
    }

    static String relativePath(StackTraceElement element) {
        String pkgName = getPackageName(element);
        String fileName = element.getFileName();
        return Index.relativePath(pkgName, fileName);
    }

    static Optional<Index> read(StackTraceElement element) throws IOException {
        String resource = relativePath(element);
        try (InputStream in = Index.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                return Optional.empty();
            }
            DataInputStream data = new DataInputStream(new BufferedInputStream(in));
            return read(data);
        }
    }

    private static String getPackageName(StackTraceElement element) {
        String className = element.getClassName();
        int pkgNameEnd = className.lastIndexOf('.');
        if (pkgNameEnd > 0) {
            return className.substring(0, pkgNameEnd);
        } else {
            return "";
        }
    }

    private static Optional<Index> read(DataInput in) throws IOException {
        if (in.readByte() != VERSION) {
            return Optional.empty();
        }
        Path source = Paths.get(in.readUTF());
        int count = in.readInt();
        NavigableSet<IndexRegion> regions = new TreeSet<>();
        for (int i = 0; i < count; i++) {
            regions.add(IndexRegion.read(in));
        }
        return Optional.of(Index.create(source, regions));
    }

    void write(
            ProcessingEnvironment env,
            CompilationUnitTree src
    ) throws IOException {
        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(
                        createIndexFile(env, src)
                                .openOutputStream()))) {
            write(out);
        }
    }

    private void write(DataOutput out) throws IOException {
        out.writeByte(VERSION);
        out.writeUTF(source().toString());
        out.writeInt(regions().size());
        for (IndexRegion element : regions()) {
            element.write(out);
        }
    }

    private static FileObject createIndexFile(
            ProcessingEnvironment env,
            CompilationUnitTree unit
    ) throws IOException {
        String name = new File(unit.getSourceFile().getName()).getName();
        return env.getFiler().createResource(
                CLASS_OUTPUT,
                "",
                Index.relativePath(unit.getPackageName().toString(), name)
        );
    }
}
