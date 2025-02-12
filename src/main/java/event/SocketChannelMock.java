package event;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Unit test helper class
 * Mock implementation for
 * @see java.nio.channels.SocketChannel
 * this class wrapper for
 * @see WritableByteChannel
 * so socket can be used passing to equvilient of socket channel and then gets what is written
 */
public class SocketChannelMock implements WritableByteChannel {

    private String output;

    public SocketChannelMock() {
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        String scrString = StandardCharsets.UTF_8.decode(src).toString();
        if (Objects.nonNull(output)) {
            output = output.concat(scrString);
        } else {
            output = scrString;
        }
        return scrString.length();
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() throws IOException {

    }

    public String getOutput() {
        return output;
    }
}
