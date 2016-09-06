package stack.source.internal;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.tools.FileObject;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import static javax.tools.Diagnostic.NOPOS;

final class StatementRegionWriter extends TreePathScanner<Void, Trees> implements Closeable {

    private final DataOutputStream out;

    StatementRegionWriter(FileObject out) throws IOException {
        this.out = new DataOutputStream(new BufferedOutputStream(
                out.openOutputStream()));
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    @Override
    public Void visitNewClass(NewClassTree node, Trees trees) {
        super.visitNewClass(node, trees);
        write(node, trees);
        return null;
    }

    @Override
    public Void visitThrow(ThrowTree node, Trees trees) {
        super.visitThrow(node, trees);
        write(node, trees);
        return null;
    }

    @Override
    public Void visitReturn(ReturnTree node, Trees trees) {
        super.visitReturn(node, trees);
        write(node, trees);
        return null;
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatementTree node, Trees trees) {
        super.visitExpressionStatement(node, trees);
        write(node, trees);
        return null;
    }

    private void write(Tree node, Trees trees) {
        SourcePositions sourcePositions = trees.getSourcePositions();
        CompilationUnitTree compilationUnit = getCurrentPath().getCompilationUnit();
        LineMap lineMap = compilationUnit.getLineMap();
        if (lineMap == null) {
            return;
        }

        long startPosition = sourcePositions.getStartPosition(compilationUnit, node);
        long endPosition = sourcePositions.getEndPosition(compilationUnit, node);
        if (startPosition == NOPOS || endPosition == NOPOS) {
            return;
        }

        long startLineNumber = lineMap.getLineNumber(startPosition);
        long endLineNumber = lineMap.getLineNumber(endPosition);
        long startLineStartPosition = lineMap.getStartPosition(startLineNumber);

        try {
            out.writeLong(startLineNumber);
            out.writeLong(endLineNumber);
            out.writeLong(startLineStartPosition);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
