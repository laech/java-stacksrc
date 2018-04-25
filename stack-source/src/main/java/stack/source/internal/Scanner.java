package stack.source.internal;

import com.sun.source.tree.*;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.annotation.processing.ProcessingEnvironment;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NavigableSet;
import java.util.TreeSet;

import static java.util.Objects.requireNonNull;
import static javax.tools.Diagnostic.NOPOS;

final class Scanner extends TreePathScanner<Void, Trees> implements Closeable {

    private final ProcessingEnvironment env;
    private final CompilationUnitTree src;
    private final NavigableSet<IndexRegion> regions;

    Scanner(
            ProcessingEnvironment env,
            CompilationUnitTree src
    ) {
        this.env = requireNonNull(env);
        this.src = requireNonNull(src);
        this.regions = new TreeSet<>();
    }

    @Override
    public void close() throws IOException {
        URI uri = src.getSourceFile().toUri();
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            Path source = Paths.get(uri).toAbsolutePath();
            Index.create(source, regions).write(env, src);
        }
    }

    @Override
    public Void visitNewClass(NewClassTree node, Trees trees) {
        super.visitNewClass(node, trees);
        add(node, trees);
        return null;
    }

    @Override
    public Void visitThrow(ThrowTree node, Trees trees) {
        super.visitThrow(node, trees);
        add(node, trees);
        return null;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatementTree node, Trees trees) {
        super.visitExpressionStatement(node, trees);
        add(node, trees);
        return null;
    }

    private void add(Tree node, Trees trees) {

        SourcePositions positions = trees.getSourcePositions();
        CompilationUnitTree unit = getCurrentPath().getCompilationUnit();
        LineMap lineMap = unit.getLineMap();
        if (lineMap == null) {
            return;
        }

        long startPos = positions.getStartPosition(unit, node);
        long endPos = positions.getEndPosition(unit, node);
        if (startPos == NOPOS || endPos == NOPOS) {
            return;
        }

        long endLineNum = lineMap.getLineNumber(endPos);
        long startLineNum = lineMap.getLineNumber(startPos);
        long startLineStartPos = lineMap.getStartPosition(startLineNum);
        regions.add(IndexRegion.create(
                startLineNum, endLineNum, startLineStartPos
        ));
    }
}
