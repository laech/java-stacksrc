package nz.lae.stacksrc;

import static java.util.Collections.newSetFromMap;
import static java.util.Objects.requireNonNull;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;

public class Throwables {
  private Throwables() {}

  public static void pruneStackTrace(Throwable throwable, Class<?> ceiling) {
    requireNonNull(throwable);
    requireNonNull(ceiling);
    for (var current : traverse(throwable)) {
      var original = current.getStackTrace();
      var pruned = prune(original, ceiling);
      if (pruned != original) {
        current.setStackTrace(pruned);
      }
    }
  }

  private static StackTraceElement[] prune(StackTraceElement[] stack, Class<?> ceiling) {
    for (var i = stack.length - 1; i >= 0; i--) {
      if (stack[i].getClassName().equals(ceiling.getName())) {
        return Arrays.copyOfRange(stack, 0, i + 1);
      }
    }
    return stack;
  }

  private static Collection<Throwable> traverse(Throwable throwable) {
    var unique = newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
    var queue = new ArrayDeque<>(List.of(throwable));
    while (!queue.isEmpty()) {
      var current = queue.poll();
      if (unique.add(current)) {
        var cause = current.getCause();
        if (cause != null) {
          queue.add(cause);
        }
        queue.addAll(List.of(current.getSuppressed()));
      }
    }
    return unique;
  }
}
