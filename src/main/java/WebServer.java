import event.EventLoop;
import event.ReadEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WebServer {

    public boolean initialized = false;
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private final List<SocketChannel> clients = new ArrayList<>();
    private final EventLoop eventLoop;

    public WebServer(int port) throws IOException {
        this.eventLoop = new EventLoop();
        initialize(port);
    }

    public void initialize(int port) throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true); //@see ServerSocketAdaptor.class
        serverChannel.socket().bind(new InetSocketAddress(port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        startEventLoop();
        initialized = true;
    }

    public int activeClientCount() {
        return clients.size();
    }

    public void close() throws IOException {
        try {
            serverChannel.close();
        } finally {
            if (serverChannel.isOpen()) {
                serverChannel.close();
            }
        }
    }

    private void startEventLoop() {
        Thread eventLoopThread = new Thread(eventLoop);
        eventLoopThread.start();
    }

    public void run() throws IOException {
        if (!initialized) {
            throw new IllegalStateException("Server not initialized");
        }
        while (true) {
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    accept(key);
                } else if (key.isReadable()) {
                    read(key);
                }
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
        clients.add(client);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        int bytesRead = client.read(buffer);
        if (bytesRead == -1) {
            //todo handle close event not here to event loop, beacuse we can close before event loop handle the left over events for this socketChannel
            // specify the socket channel removed, and remove event loop, just remove client list here and then rest of event loop handles
    /*        client.close();
            key.cancel();
            clients.remove(client);*/
        } else {
            eventLoop.add(new ReadEvent(buffer, client));
        }
    }
}
