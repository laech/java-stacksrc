package stack.source.junit4;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@Ignore("Test intended to be viewed by a human in an IDE," +
        " to compare the differences with and without" +
        " an error decorator.")
public final class ManualInspectionTest {

    @Rule
    public final ErrorDecorator errorDecorator = new ErrorDecorator();

    @Test
    public void compareInts() {
        assertEquals(1, 2);
    }

    @Test
    public void compareStrings() {
        assertEquals("bob", "bab");
    }

    @Test
    public void compareLongStrings() {
        assertEquals(
                "2" + String.join("\n", IntStream.range(1, 100)
                        .mapToObj(String::valueOf)
                        .collect(toList())),
                "2"
        );
    }

    @Test
    public void compareArrays() {
        assertArrayEquals(
                new String[]{"111", "112", "113"},
                new String[]{"111", "112", "114"}
        );
    }
}
