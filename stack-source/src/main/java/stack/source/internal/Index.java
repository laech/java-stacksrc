package stack.source.internal;

import com.google.auto.value.AutoValue;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.joining;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

@AutoValue
abstract class Index {

    private static final byte VERSION = 2;

    Index() {
    }

    static Index create(Path source, long sourceModTime, Set<IndexRegion> regions) {
        return new AutoValue_Index(
                source.toAbsolutePath(),
                sourceModTime,
                unmodifiableSet(regions)
        );
    }

    abstract Path source();

    abstract long sourceModTime();

    abstract Set<IndexRegion> regions();

    private static String relativePath(String pkgName, String fileName) {
        return Stream.of("stack-source", pkgName, fileName)
                .filter(s -> s != null && !s.isEmpty())
                .collect(joining("/", "", ".index"));
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
            DataInputStream data = new DataInputStream(new GZIPInputStream(in));
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
        long sourceModTime = in.readLong();
        int count = in.readInt();
        Set<IndexRegion> regions = new HashSet<>();
        for (int i = 0; i < count; i++) {
            regions.add(IndexRegion.read(in));
        }
        return Optional.of(Index.create(source, sourceModTime, regions));
    }

    void write(
            ProcessingEnvironment env,
            FileObject src,
            String pkg
    ) throws IOException {
        try (DataOutputStream out = new DataOutputStream(
                new GZIPOutputStream(
                        createIndexFile(env, src, pkg)
                                .openOutputStream()))) {
            write(out);
        }
    }

    private void write(DataOutput out) throws IOException {
        out.writeByte(VERSION);
        out.writeUTF(source().toString());
        out.writeLong(sourceModTime());
        out.writeInt(regions().size());
        for (IndexRegion element : regions()) {
            element.write(out);
        }
    }

    private static FileObject createIndexFile(
            ProcessingEnvironment env,
            FileObject unit,
            String pkg
    ) throws IOException {
        String name = new File(unit.getName()).getName();
        return env.getFiler().createResource(
                CLASS_OUTPUT,
                "",
                Index.relativePath(pkg, name)
        );
    }
}
