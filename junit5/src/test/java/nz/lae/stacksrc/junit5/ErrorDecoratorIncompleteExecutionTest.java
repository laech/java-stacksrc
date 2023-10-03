package nz.lae.stacksrc.junit5;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.opentest4j.IncompleteExecutionException;
import org.opentest4j.TestSkippedException;

@ExtendWith({ErrorDecoratorIncompleteExecutionTest.AssertDecoration.class, ErrorDecorator.class})
class ErrorDecoratorIncompleteExecutionTest {

  @Test
  @Disabled
  void ignoresDisabled() {}

  @Test
  void ignoresTestSkippedException() {
    throw new TestSkippedException();
  }

  @Test
  @SuppressWarnings("DataFlowIssue")
  void ignoresTestAbortedException() {
    assumeTrue(false);
  }

  static class AssertDecoration implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable e) {
      // Not decorated
      assertInstanceOf(IncompleteExecutionException.class, e);
    }
  }
}
