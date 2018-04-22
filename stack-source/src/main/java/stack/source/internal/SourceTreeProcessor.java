package stack.source.internal;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singleton;
import static javax.tools.Diagnostic.Kind.WARNING;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

public final class SourceTreeProcessor extends AbstractProcessor {

    // TODO add version, alway override index on different version

    private final Set<CompilationUnitTree> units = new HashSet<>();
    private Trees trees;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return singleton("*");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment process) {
        super.init(process);
        trees = Trees.instance(process);
    }

    @Override
    public boolean process(
            Set<? extends TypeElement> annotations,
            RoundEnvironment round
    ) {
        try {
            doProcess(round);
        } catch (Throwable e) {
            logWarning(e);
        }
        return false;
    }

    private void doProcess(RoundEnvironment round) throws IOException {
        if (round.processingOver()) {
            processCompilationUnits();
        } else {
            collectCompilationUnits(round);
        }
    }

    private void collectCompilationUnits(RoundEnvironment roundEnv) {
        roundEnv.getRootElements().stream()
                .map(trees::getPath)
                .map(TreePath::getCompilationUnit)
                .forEach(units::add);
    }

    private void processCompilationUnits() throws IOException {
        for (CompilationUnitTree unit : units) {
            processCompilationUnit(unit);
        }
        units.clear();
    }

    private void processCompilationUnit(CompilationUnitTree unit) throws IOException {
        FileObject src = unit.getSourceFile();
        copy(src, createDestinationFile(unit, src));
        index(unit, createIndexFile(unit, src));
    }

    private FileObject createDestinationFile(
            CompilationUnitTree unit,
            FileObject src
    ) throws IOException {
        return processingEnv.getFiler().createResource(
                CLASS_OUTPUT,
                unit.getPackageName().toString(),
                new File(src.getName()).getName()
        );
    }

    private FileObject createIndexFile(
            CompilationUnitTree unit,
            FileObject src
    ) throws IOException {
        return processingEnv.getFiler().createResource(
                CLASS_OUTPUT,
                unit.getPackageName().toString(),
                new File(src.getName()).getName() + ".index"
        );
    }

    private void copy(FileObject src, FileObject dst) throws IOException {
        try (InputStream in = src.openInputStream();
             OutputStream out = dst.openOutputStream()) {
            byte[] buf = new byte[1024];
            int count;
            while ((count = in.read(buf)) != -1) {
                out.write(buf, 0, count);
            }
        }
    }

    private void index(CompilationUnitTree unit, FileObject index) throws IOException {
        try (StatementRegionWriter writer = new StatementRegionWriter(index)) {
            writer.scan(unit, trees);
        }
    }

    private void logWarning(Throwable e) {
        try {
            processingEnv.getMessager()
                    .printMessage(WARNING, getStackTrace(e));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private String getStackTrace(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }

}
