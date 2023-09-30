package nz.lae.stacksrc.core.same1;

import nz.lae.stacksrc.core.StackTraceDecoratorTest;

/** Same name as {@link nz.lae.stacksrc.core.same2.Same} */
public final class Same implements StackTraceDecoratorTest.DoThrow {
  @Override
  public void doThrow() throws MyException {
    throw new MyException("same1");
  }
}
