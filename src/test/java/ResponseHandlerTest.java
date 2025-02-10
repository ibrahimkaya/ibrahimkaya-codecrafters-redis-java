import handler.ResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
class ResponseHandlerTest {

    private ResponseHandler responseHandler;

    @BeforeEach
    void setUp() {
        responseHandler = new ResponseHandler();
    }

    @Test
    void shouldReturnPingPong() throws IOException {
        String ping = "*1\r\n$4\r\nPING\r\n";
        InputStream inputStream = new ByteArrayInputStream(ping.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        responseHandler.handle(br, outputStream);
        assertEquals("+PONG\r\n", outputStream.toString());
    }

    @Test
    void shouldReturnMultiplePingPong() throws IOException {
        String ping = "*2\r\n$4\r\nPING\r\n$4\r\nPING\r\n";
        InputStream inputStream = new ByteArrayInputStream(ping.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        responseHandler.handle(br, outputStream);
        assertEquals("+PONG\r\n+PONG\r\n", outputStream.toString());
    }
}