package stack.source.junit5;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("Test intended to be viewed by a human in an IDE," +
        " to compare the differences with and without" +
        " an error decorator.")
@ExtendWith(ErrorDecorator.class)
final class ManualInspectionTest {

    @Test
    void compareInts() {
        assertEquals(1, 2);
    }

    @Test
    void compareStrings() {
        assertEquals("bob", "bab");
    }

    @Test
    void compareLongStrings() {
        assertEquals(
                "2" + String.join("\n", IntStream.range(1, 100)
                        .mapToObj(String::valueOf)
                        .collect(toList())),
                "2"
        );
    }

    @Test
    void compareArrays() {
        assertArrayEquals(
                new String[]{"111", "112", "113"},
                new String[]{"111", "112", "114"}
        );
    }
}
