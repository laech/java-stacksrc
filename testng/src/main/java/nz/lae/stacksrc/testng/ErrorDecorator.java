package nz.lae.stacksrc.testng;

import nz.lae.stacksrc.DecoratedAssertionError;
import org.testng.ITestListener;
import org.testng.ITestResult;

public final class ErrorDecorator implements ITestListener {

  @Override
  public void onTestFailure(ITestResult result) {
    var throwable = result.getThrowable();
    if (throwable == null || throwable instanceof DecoratedAssertionError) {
      return;
    }
    result.setThrowable(new DecoratedAssertionError(throwable));
  }
}
