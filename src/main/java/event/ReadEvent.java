package event;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

//https://app.codecrafters.io/walkthroughs/redis-event-loop
//https://www.youtube.com/watch?v=gLfuZrrfKes&ab_channel=WittCode
public record ReadEvent(@Nonnull ByteBuffer readByte, @Nonnull SocketChannel client) {
}
