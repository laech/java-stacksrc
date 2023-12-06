package nz.lae.stacksrc.logback;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import nz.lae.stacksrc.StackTraceDecorator;

public final class DecoratedThrowableProxyConverter extends ThrowableProxyConverter {

  @Override
  protected String throwableProxyToString(IThrowableProxy tp) {
    if (tp instanceof ThrowableProxy) {
      return StackTraceDecorator.get().decorate(((ThrowableProxy) tp).getThrowable());
    }
    return super.throwableProxyToString(tp);
  }
}
