package stack.source.internal;

import com.sun.source.tree.*;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.nio.file.Files.getLastModifiedTime;
import static java.util.Objects.requireNonNull;
import static javax.tools.Diagnostic.NOPOS;

final class SourceScanner extends TreePathScanner<Void, Trees> {

    private final ProcessingEnvironment env;
    private final Map<CompilationUnitTree, Set<IndexRegion>> regions;

    SourceScanner(ProcessingEnvironment env) {
        this.env = requireNonNull(env);
        this.regions = new HashMap<>();
    }

    void flush() {
        regions.forEach((unit, regions) -> {
            FileObject file = unit.getSourceFile();
            Path path = Paths.get(file.toUri()).toAbsolutePath();
            try {
                long sourceModTime = getLastModifiedTime(path).toMillis();
                String pkg = getPackageName(unit);
                Index.create(path, sourceModTime, regions).write(env, file, pkg);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        regions.clear();
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree node, Trees trees) {
        URI uri = node.getSourceFile().toUri();
        if (!"file".equalsIgnoreCase(uri.getScheme())) {
            return null;
        }
        return super.visitCompilationUnit(node, trees);
    }

    @Override
    public Void visitMethod(MethodTree node, Trees trees) {
        add(node, trees);
        return super.visitMethod(node, trees);
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

    @Override
    public Void visitVariable(VariableTree node, Trees trees) {
        add(node, trees);
        return super.visitVariable(node, trees);
    }

    private void add(Tree node, Trees trees) {
        CompilationUnitTree unit = getCurrentPath().getCompilationUnit();
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
        regions.computeIfAbsent(unit, __ -> new HashSet<>())
                .add(IndexRegion.create(startLineNum, endLineNum, startLineStartPos));
    }

    private static String getPackageName(CompilationUnitTree unit) {
        ExpressionTree pkg = unit.getPackageName();
        return pkg == null ? "" : pkg.toString();
    }
}
