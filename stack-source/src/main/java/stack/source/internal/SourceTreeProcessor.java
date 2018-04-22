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
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singleton;
import static javax.tools.Diagnostic.Kind.NOTE;
import static javax.tools.Diagnostic.Kind.WARNING;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

public final class SourceTreeProcessor extends AbstractProcessor {

    // TODO add version, alway override index on different version

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
            RoundEnvironment roundEnv) {

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
            Iterator<CompilationUnitTree> iterator = compilationUnits.iterator();
            while (iterator.hasNext()) {
                CompilationUnitTree compilationUnit = iterator.next();
                processingEnv.getMessager().printMessage(NOTE, compilationUnit.getSourceFile().getName());
                FileObject src = compilationUnit.getSourceFile();
                FileObject dst = processingEnv.getFiler().createResource(
                        CLASS_OUTPUT,
                        compilationUnit.getPackageName().toString(),
                        new File(src.getName()).getName());
                try (OutputStream out = dst.openOutputStream()) {
                    out.write(src.getCharContent(true).toString().getBytes(UTF_8));
                }
                FileObject index = processingEnv.getFiler().createResource(
                        CLASS_OUTPUT,
                        compilationUnit.getPackageName().toString(),
                        new File(src.getName()).getName() + ".index");
                try (StatementRegionWriter writer = new StatementRegionWriter(index)) {
                    writer.scan(compilationUnit, trees);
                }
                iterator.remove();
            }
            return;
        }

        for (Element element : roundEnv.getRootElements()) {
            compilationUnits.add(trees.getPath(element).getCompilationUnit());
        }

    }

    private void logWarning(Throwable e) {
        try {
            processingEnv.getMessager()
                    .printMessage(WARNING, getStackTrace(e));
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
