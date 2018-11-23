package stack.source.internal;

import com.google.auto.service.AutoService;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static javax.tools.Diagnostic.Kind.NOTE;
import static javax.tools.Diagnostic.Kind.WARNING;
import static stack.source.internal.Throwables.getStackTraceAsString;

@AutoService(Processor.class)
public final class SourceProcessor extends AbstractProcessor {

    private static final Set<String> supportedAnnotations = unmodifiableSet(new HashSet<>(asList(
            "org.junit.Test",
            "org.junit.jupiter.api.Test"
    )));

    private final Set<CompilationUnitTree> units = new HashSet<>();
    private SourceScanner scanner;
    private Trees trees;
    private boolean enabled = true;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return supportedAnnotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment process) {
        super.init(process);

        scanner = new SourceScanner(process);
        try {
            trees = Trees.instance(process);
        } catch (IllegalArgumentException e) {
            enabled = false;

            // Eclipse users will hit this
            Messager messager = process.getMessager();
            messager.printMessage(WARNING, getClass().getName() + ": " +
                    "Current processing environment '" + process + "' " +
                    "is not supported by the Java Compiler Tree API, " +
                    "no stack trace will be decorated."
            );
        }
    }

    @Override
    public boolean process(
            Set<? extends TypeElement> annotations,
            RoundEnvironment round
    ) {
        if (!enabled) {
            return false;
        }
        try {
            doProcess(round);
        } catch (Throwable e) {
            logWarning(e);
        }
        return false;
    }

    private void doProcess(RoundEnvironment round) {
        collectCompilationUnits(round);
        if (round.processingOver()) {
            processCompilationUnits();
        }
        scanner.flush();
    }

    private void collectCompilationUnits(RoundEnvironment roundEnv) {
        for (String annotation : supportedAnnotations) {
            TypeElement type = processingEnv.getElementUtils().getTypeElement(annotation);
            if (type != null) {
                roundEnv.getElementsAnnotatedWith(type).stream()
                        .map(trees::getPath)
                        .map(TreePath::getCompilationUnit)
                        .forEach(units::add);
            }
        }
    }

    private void processCompilationUnits() {
        units.forEach(unit -> scanner.scan(unit, trees));
        units.clear();
    }

    private void logWarning(Throwable e) {
        try {
            processingEnv.getMessager()
                    .printMessage(WARNING, getStackTraceAsString(e));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
