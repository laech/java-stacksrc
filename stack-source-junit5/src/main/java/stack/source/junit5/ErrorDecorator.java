package stack.source.junit5;

import com.google.auto.service.AutoService;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.opentest4j.IncompleteExecutionException;
import stack.source.internal.DecoratedError;

@AutoService(Extension.class)
public final class ErrorDecorator implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(
            ExtensionContext context,
            Throwable throwable
    ) throws Throwable {

        if (throwable instanceof IncompleteExecutionException) {
            throw throwable;
        }
        throw new DecoratedError(throwable);
    }

}
