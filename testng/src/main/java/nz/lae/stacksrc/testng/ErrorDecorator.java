package nz.lae.stacksrc.testng;

import nz.lae.stacksrc.DecoratedAssertionError;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public final class ErrorDecorator implements IInvokedMethodListener {

  @Override
  public void afterInvocation(IInvokedMethod method, ITestResult result) {
    var throwable = result.getThrowable();
    if (throwable == null || throwable instanceof DecoratedAssertionError) {
      return;
    }
    result.setThrowable(new DecoratedAssertionError(throwable));
  }
}
