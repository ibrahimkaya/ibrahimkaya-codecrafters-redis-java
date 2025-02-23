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
        String echo = "*2\r\n$4\r\nECHO\r\n$15\r\nrandomStringHey\r\n";
        ByteBuffer inputBytes = ByteBuffer.wrap(echo.getBytes());
        SocketChannelMock socketChannel = new SocketChannelMock();
        responseHandler.handle(inputBytes, socketChannel);
        assertEquals("$15\r\nrandomStringHey\r\n", socketChannel.getOutput());
    }

    @Test
    void shouldSetCommandReturnOkAndGetReturnSetValue() throws IOException {
        String set = "*3\r\n$3\r\nSET\r\n$5\r\ngrape\r\n$10\r\nstrawberry\r\n";
        ByteBuffer inputBytes = ByteBuffer.wrap(set.getBytes());
        SocketChannelMock socketChannel = new SocketChannelMock();
        responseHandler.handle(inputBytes, socketChannel);
        assertEquals("+OK\r\n", socketChannel.getOutput());

        String get = "*2\r\n$3\r\nGET\r\n$5\r\ngrape\r\n";
        ByteBuffer getInputBytes = ByteBuffer.wrap(get.getBytes());
        responseHandler.handle(getInputBytes, socketChannel);
        //the mock socket channel append output, so for this case we expect set and get value appended
        assertEquals("+OK\r\n$10\r\nstrawberry\r\n", socketChannel.getOutput());
    }

    @Test
    void shouldEmptyGetValueReturnNullBulkString() throws IOException {
        String get = "*3\r\n$3\r\nGET\r\n$5\r\nnotExistsKey\r\n";
        SocketChannelMock socketChannel = new SocketChannelMock();
        ByteBuffer getInputBytes = ByteBuffer.wrap(get.getBytes());
        responseHandler.handle(getInputBytes, socketChannel);
        assertEquals("$-1\r\n", socketChannel.getOutput());
    }
}