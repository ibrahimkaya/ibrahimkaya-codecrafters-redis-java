package event;

import java.io.BufferedReader;
import java.nio.channels.SocketChannel;

//https://app.codecrafters.io/walkthroughs/redis-event-loop
//https://www.youtube.com/watch?v=gLfuZrrfKes&ab_channel=WittCode
public record WriteEvent(BufferedReader bytesToWrite, SocketChannel client) {
}
