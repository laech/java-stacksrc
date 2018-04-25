package stack.source.junit5;

import com.google.auto.service.AutoService;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.opentest4j.IncompleteExecutionException;

@AutoService(Extension.class)
public final class ErrorDecorator implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(
            ExtensionContext context,
            Throwable e
    ) throws Throwable {

        if (e instanceof IncompleteExecutionException) {
            throw e;
        }
        throw DecoratedAssertionFailedError.create(e);
    }

}
