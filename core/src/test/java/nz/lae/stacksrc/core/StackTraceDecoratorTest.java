package nz.lae.stacksrc.core;

import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class StackTraceDecoratorTest {

  @ParameterizedTest
  @MethodSource("filesWithSameNameArgs")
  void resolvesFilesWithSameName(DoThrow instance, String message) throws Exception {
    var exception = assertThrows(DoThrow.MyException.class, instance::doThrow);
    var element =
        stream(exception.getStackTrace())
            .filter(it -> it.getMethodName().equals("doThrow"))
            .findFirst()
            .orElseThrow();

    var actual = StackTraceDecorator.decorate(element).orElseThrow();
    var expected =
        String.join(
            System.lineSeparator(),
            "\t    7    @Override",
            "\t    8    public void doThrow() throws MyException {",
            "\t->  9      throw new MyException(\"" + message + "\");",
            "\t   10    }",
            "\t   11  }");

    assertEquals(expected, actual);
  }

  private static Object[][] filesWithSameNameArgs() {
    return new Object[][] {
      {new nz.lae.stacksrc.core.same1.Same(), "same1"},
      {new nz.lae.stacksrc.core.same2.Same(), "same2"},
    };
  }

  public interface DoThrow {
    class MyException extends Exception {
      public MyException(String message) {
        super(message);
      }
    }

    void doThrow() throws MyException;
  }
}
