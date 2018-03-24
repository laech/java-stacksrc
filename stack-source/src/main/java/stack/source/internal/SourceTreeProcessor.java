package stack.source.internal;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.Trees;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singleton;
import static javax.tools.Diagnostic.Kind.NOTE;
import static javax.tools.Diagnostic.Kind.WARNING;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

public final class SourceTreeProcessor extends AbstractProcessor {

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
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
    }

    @Override
    public boolean process(
            Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv
    ) {
        try {
            doProcess(roundEnv);
        } catch (Throwable e) {
            logWarning(e);
        }
        return false;
    }

    private final Set<CompilationUnitTree> compilationUnits = new HashSet<>();

    private void doProcess(RoundEnvironment roundEnv) throws IOException {

        if (roundEnv.processingOver()) {
            for (CompilationUnitTree unit : compilationUnits) {
                write(unit);
            }
            compilationUnits.clear();
            return;
        }

        for (Element element : roundEnv.getRootElements()) {
            compilationUnits.add(trees.getPath(element).getCompilationUnit());
        }
    }

    private void write(CompilationUnitTree unit) throws IOException {
        processingEnv.getMessager().printMessage(NOTE, unit.getSourceFile().getName());
        FileObject src = unit.getSourceFile();
        FileObject dst = getTarget(unit);
        copy(src, dst);
        FileObject index = processingEnv.getFiler().createResource(
                CLASS_OUTPUT,
                unit.getPackageName().toString(),
                new File(src.getName()).getName() + ".index");
        try (StatementRegionWriter writer = new StatementRegionWriter(index)) {
            writer.scan(unit, trees);
        }
    }

    private FileObject getTarget(CompilationUnitTree unit) throws IOException {
        return processingEnv.getFiler().createResource(
                CLASS_OUTPUT,
                unit.getPackageName().toString(),
                new File(unit.getSourceFile().getName()).getName()
        );
    }

    private void copy(FileObject src, FileObject dst) throws IOException {
        try (InputStream in = src.openInputStream();
             OutputStream out = dst.openOutputStream()) {
            byte[] buffer = new byte[4096];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
        }
    }

    private void logWarning(Throwable e) {
        try {
            processingEnv.getMessager().printMessage(WARNING, getStackTrace(e));
        } catch (Throwable ignore) {
            ignore.printStackTrace();
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
