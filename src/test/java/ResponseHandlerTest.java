import event.SocketChannelMock;
import handler.ResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResponseHandlerTest {

    private ResponseHandler responseHandler;

    @BeforeEach
    void setUp() {
        responseHandler = new ResponseHandler();
    }

    @Test
    void shouldReturnPingPong() throws IOException {
        String ping = "*1\r\n$4\r\nPING\r\n";
        ByteBuffer inputBytes = ByteBuffer.wrap(ping.getBytes());
        SocketChannelMock socketChannel = new SocketChannelMock();
        responseHandler.handle(inputBytes, socketChannel);
        assertEquals("+PONG\r\n", socketChannel.getOutput());
    }

    @Test
    void shouldReturnMultiplePingPong() throws IOException {
        String ping = "*2\r\n$4\r\nPING\r\n$4\r\nPING\r\n";
        ByteBuffer inputBytes = ByteBuffer.wrap(ping.getBytes());
        SocketChannelMock socketChannel = new SocketChannelMock();
        responseHandler.handle(inputBytes, socketChannel);
        assertEquals("+PONG\r\n+PONG\r\n", socketChannel.getOutput());
    }

    @Test
    void shouldReturnEchoedStrings() throws IOException {
        String echo = "*2\r\n$4\r\nECHO\r\n$3\r\nhey\r\n";
        ByteBuffer inputBytes = ByteBuffer.wrap(echo.getBytes());
        SocketChannelMock socketChannel = new SocketChannelMock();
        responseHandler.handle(inputBytes, socketChannel);
        assertEquals("$3\r\nhey\r\n", socketChannel.getOutput());
    }

    @Test
    void shouldReturnEchoedStrings1() throws IOException {
        String echo = "*2\r\n$4\r\nECHO\r\n$3\r\nrandomStringHey\r\n";
        ByteBuffer inputBytes = ByteBuffer.wrap(echo.getBytes());
        SocketChannelMock socketChannel = new SocketChannelMock();
        responseHandler.handle(inputBytes, socketChannel);
        assertEquals("$15\r\nrandomStringHey\r\n", socketChannel.getOutput());
    }
}