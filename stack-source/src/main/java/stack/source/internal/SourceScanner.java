package stack.source.internal;

import com.sun.source.tree.*;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

import javax.annotation.processing.ProcessingEnvironment;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static javax.tools.Diagnostic.NOPOS;

final class SourceScanner extends TreeScanner<Void, Trees> {

    private final ProcessingEnvironment env;
    private final Set<IndexRegion> regions;
    private CompilationUnitTree unit;

    SourceScanner(ProcessingEnvironment env) {
        this.env = requireNonNull(env);
        this.regions = new HashSet<>();
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree node, Trees trees) {
        URI uri = node.getSourceFile().toUri();
        if (!"file".equalsIgnoreCase(uri.getScheme())) {
            return null;
        }
        unit = node;
        regions.clear();
        super.visitCompilationUnit(node, trees);
        Path source = Paths.get(uri).toAbsolutePath();
        try {
            Index.create(source, regions).write(env, node);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            unit = null;
        }
        return null;
    }

    @Override
    public Void visitMethod(MethodTree node, Trees trees) {
        add(node, trees);
        return super.visitMethod(node, trees);
    }

    @Override
    public Void visitBlock(BlockTree node, Trees trees) {
        add(node, trees);
        return super.visitBlock(node, trees);
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree node, Trees trees) {
        add(node, trees);
        return super.visitDoWhileLoop(node, trees);
    }

    @Override
    public Void visitWhileLoop(WhileLoopTree node, Trees trees) {
        add(node, trees);
        return super.visitWhileLoop(node, trees);
    }

    @Override
    public Void visitForLoop(ForLoopTree node, Trees trees) {
        add(node, trees);
        return super.visitForLoop(node, trees);
    }

    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree node, Trees trees) {
        add(node, trees);
        return super.visitEnhancedForLoop(node, trees);
    }

    @Override
    public Void visitSwitch(SwitchTree node, Trees trees) {
        add(node, trees);
        return super.visitSwitch(node, trees);
    }

    @Override
    public Void visitCase(CaseTree node, Trees trees) {
        add(node, trees);
        return super.visitCase(node, trees);
    }

    @Override
    public Void visitSynchronized(SynchronizedTree node, Trees trees) {
        add(node, trees);
        return super.visitSynchronized(node, trees);
    }

    @Override
    public Void visitTry(TryTree node, Trees trees) {
        add(node, trees);
        return super.visitTry(node, trees);
    }

    @Override
    public Void visitCatch(CatchTree node, Trees trees) {
        add(node, trees);
        return super.visitCatch(node, trees);
    }

    @Override
    public Void visitConditionalExpression(ConditionalExpressionTree node, Trees trees) {
        add(node, trees);
        return super.visitConditionalExpression(node, trees);
    }

    @Override
    public Void visitIf(IfTree node, Trees trees) {
        add(node, trees);
        return super.visitIf(node, trees);
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatementTree node, Trees trees) {
        add(node, trees);
        return super.visitExpressionStatement(node, trees);
    }

    @Override
    public Void visitThrow(ThrowTree node, Trees trees) {
        add(node, trees);
        return super.visitThrow(node, trees);
    }

    @Override
    public Void visitAssert(AssertTree node, Trees trees) {
        add(node, trees);
        return super.visitAssert(node, trees);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree node, Trees trees) {
        add(node, trees);
        return super.visitMethodInvocation(node, trees);
    }

    @Override
    public Void visitNewClass(NewClassTree node, Trees trees) {
        add(node, trees);
        return super.visitNewClass(node, trees);
    }

    @Override
    public Void visitNewArray(NewArrayTree node, Trees trees) {
        add(node, trees);
        return super.visitNewArray(node, trees);
    }

    @Override
    public Void visitLambdaExpression(LambdaExpressionTree node, Trees trees) {
        add(node, trees);
        return super.visitLambdaExpression(node, trees);
    }

    @Override
    public Void visitAssignment(AssignmentTree node, Trees trees) {
        add(node, trees);
        return super.visitAssignment(node, trees);
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree node, Trees trees) {
        add(node, trees);
        return super.visitCompoundAssignment(node, trees);
    }

    @Override
    public Void visitUnary(UnaryTree node, Trees trees) {
        add(node, trees);
        return super.visitUnary(node, trees);
    }

    @Override
    public Void visitBinary(BinaryTree node, Trees trees) {
        add(node, trees);
        return super.visitBinary(node, trees);
    }

    @Override
    public Void visitTypeCast(TypeCastTree node, Trees trees) {
        add(node, trees);
        return super.visitTypeCast(node, trees);
    }

    @Override
    public Void visitInstanceOf(InstanceOfTree node, Trees trees) {
        add(node, trees);
        return super.visitInstanceOf(node, trees);
    }

    @Override
    public Void visitArrayAccess(ArrayAccessTree node, Trees trees) {
        add(node, trees);
        return super.visitArrayAccess(node, trees);
    }

    private void add(Tree node, Trees trees) {
        if (unit == null) {
            return;
        }
        SourcePositions positions = trees.getSourcePositions();
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
