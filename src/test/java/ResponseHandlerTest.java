import event.SocketChannelMock;
import handler.ResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseHandlerTest {

    private ResponseHandler responseHandler;

    @BeforeEach
    void setUp() {
        responseHandler = new ResponseHandler();
    }

    @Test
    void shouldReturnPingPong() throws IOException {
        String ping = "*1\r\n$4\r\nPING\r\n";
        SocketChannelMock socketChannel = new SocketChannelMock();
        responseHandler.handle(ping, socketChannel);
        assertEquals("+PONG\r\n", socketChannel.getOutput());
    }

    @Test
    void shouldReturnMultiplePingPong() throws IOException {
        String ping = "*2\r\n$4\r\nPING\r\n$4\r\nPING\r\n";
        SocketChannelMock socketChannel = new SocketChannelMock();
        responseHandler.handle(ping, socketChannel);
        assertEquals("+PONG\r\n+PONG\r\n", socketChannel.getOutput());
    }

    @Test
    void shouldReturnEchoedStrings() throws IOException {
        String echo = "*2\r\n$4\r\nECHO\r\n$3\r\nhey\r\n";
        SocketChannelMock socketChannel = new SocketChannelMock();
        responseHandler.handle(echo, socketChannel);
        assertEquals("$3\r\nhey\r\n", socketChannel.getOutput());
    }

    @Test
    void shouldReturnEchoedStrings1() throws IOException {
        String echo = "*2\r\n$4\r\nECHO\r\n$15\r\nrandomStringHey\r\n";
        SocketChannelMock socketChannel = new SocketChannelMock();
        responseHandler.handle(echo, socketChannel);
        assertEquals("$15\r\nrandomStringHey\r\n", socketChannel.getOutput());
    }

    @Test
    void shouldSetCommandReturnOkAndGetReturnSetValue() throws IOException {
        String set = "*3\r\n$3\r\nSET\r\n$5\r\ngrape\r\n$10\r\nstrawberry\r\n";
        SocketChannelMock socketChannel = new SocketChannelMock();
        responseHandler.handle(set, socketChannel);
        assertEquals("+OK\r\n", socketChannel.getOutput());

        String get = "*2\r\n$3\r\nGET\r\n$5\r\ngrape\r\n";
        responseHandler.handle(get, socketChannel);
        //the mock socket channel append output, so for this case we expect set and get value appended
        assertEquals("+OK\r\n$10\r\nstrawberry\r\n", socketChannel.getOutput());
    }

    @Test
    void shouldEmptyGetValueReturnNullBulkString() throws IOException {
        String get = "*3\r\n$3\r\nGET\r\n$5\r\nnotExistsKey\r\n";
        SocketChannelMock socketChannel = new SocketChannelMock();
        ByteBuffer getInputBytes = ByteBuffer.wrap(get.getBytes());
        responseHandler.handle(get, socketChannel);
        assertEquals("$-1\r\n", socketChannel.getOutput());
    }
}