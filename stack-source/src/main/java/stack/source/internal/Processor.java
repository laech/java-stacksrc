package stack.source.internal;

import com.google.auto.service.AutoService;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singleton;
import static javax.tools.Diagnostic.Kind.WARNING;

@AutoService(javax.annotation.processing.Processor.class)
public final class Processor extends AbstractProcessor {

    // TODO add version, always override index on different version
    // TODO use original source file instead of copy?

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
            try (Scanner writer = new Scanner(processingEnv, unit)) {
                writer.scan(unit, trees);
            }
        }
        units.clear();
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
