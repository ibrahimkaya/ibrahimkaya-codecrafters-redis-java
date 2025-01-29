package parser;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static parser.Serialization.*;

class SerializationTest {

    @Test
    public void shouldDecorateArray() {
        var expectedOutput = "*2\r\n$5\r\nhello\r\n$5\r\nworld\r\n".getBytes();
        var output = decorateArray("hello", "world");
       assertArrayEquals(expectedOutput, output);
    }
}