package event;

import javax.annotation.Nonnull;
import java.nio.channels.SocketChannel;

//https://app.codecrafters.io/walkthroughs/redis-event-loop
//https://www.youtube.com/watch?v=gLfuZrrfKes&ab_channel=WittCode
public record ReadEvent(String readString, @Nonnull SocketChannel client, boolean closeEvent) {

    public ReadEvent(String readString, @Nonnull SocketChannel client) {
        this(readString, client, false);
    }
}
