package event;

import handler.ResponseHandler;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class EventLoop implements Runnable {
    private final static LinkedBlockingQueue<ReadEvent> eventQueue;
    private final ResponseHandler responseHandler;

    public EventLoop() {
        this.responseHandler = new ResponseHandler();
    }

    static {
        eventQueue = new LinkedBlockingQueue<>();
    }

    public void add(ReadEvent event) {
        eventQueue.add(event);
    }

    @Override
    public void run() {
        try {
            handleEvent();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("exception while event loop running", e);
        }
    }

    private void handleEvent() throws IOException, InterruptedException {
        while (true) { //pool method is waiting to any item to fill in eventQueue so there is no "busy waiting"
            ReadEvent event = eventQueue.poll(1, TimeUnit.DAYS);
            responseHandler.handle(event.readByte(), event.client());
        }
    }
}
