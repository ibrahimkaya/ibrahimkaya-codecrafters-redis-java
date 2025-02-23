package handler;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.nio.ByteBuffer.wrap;
import static java.util.Objects.nonNull;
import static parser.Serialization.decorateBulkString;
import static parser.Serialization.decorateString;

public class ResponseHandler {
    private static final Logger logger = Logger.getLogger(ResponseHandler.class);

    private final Map<String, String> basicInMemoryKeyValueStore;

    public ResponseHandler() {
        basicInMemoryKeyValueStore = new HashMap<>();
    }

    //line handler'a bir stage ekleyebilirsin, örneğin ECHO gibi, bunu iteratif şekilde line handler'a paslarız while
    // içerisinden, eğer bir satır output yazarda mevcut döngüsünden çıkarsa o zaman tekrar sıfırdan yeni command execute eder gibi davranır
    //örneğin string okudu -> echo oldu, durumu echoya setler bırakır -> durum echo ise mevcut line'ı basar while içinde ve state'i sıfırlar
    public void handle(ByteBuffer readByte, WritableByteChannel socketChannel) throws IOException {
        Iterator<String> lineIterator = new String(readByte.array()).lines().iterator();
        while (lineIterator.hasNext()) {
            lineHandler(socketChannel, lineIterator);
        }
        readByte.flip();
    }

    private enum State {
        ARRAY, BULK_STRING, STRING, ECHO,
    }

    private void lineHandler(WritableByteChannel socketChannel, Iterator<String> lineIterator) throws IOException {
        String line = lineIterator.next();
        logger.info("readed line: " + line);
        char startingChar = line.charAt(0);
        switch (startingChar) {
            case '*' -> logger.info("Parsing array, skipping...");
            case '$' -> handleBulkString(lineIterator.next(), socketChannel, lineIterator);
            case '+' -> logger.info("Parsing string");
            default -> logger.info("Unknown character: " + startingChar);
        }
    }

    private void handleBulkString(String input, WritableByteChannel socketChannel, Iterator<String> lineIterator) throws IOException {
        logger.info("Parsing bulk string");
        switch (input) {
            case "PING" -> socketChannel.write(wrap(decorateString("PONG")));
            case "ECHO" -> handleEcho(socketChannel, lineIterator);
            case "SET" -> handleSet(socketChannel, lineIterator);
            case "GET" -> handleGet(socketChannel, lineIterator);
        }

    }

    private void handleEcho(WritableByteChannel socketChannel, Iterator<String> lineIterator) throws IOException {
        var echo = extractBulkString(lineIterator);
        socketChannel.write(wrap(decorateBulkString(echo)));
    }

    private void handleSet(WritableByteChannel socketChannel, Iterator<String> lineIterator) throws IOException {
        var key = extractBulkString(lineIterator);
        var value = extractBulkString(lineIterator);
        basicInMemoryKeyValueStore.put(key, value);
        socketChannel.write(wrap(decorateString("OK")));
    }

    private void handleGet(WritableByteChannel socketChannel, Iterator<String> lineIterator) throws IOException {
        var key = extractBulkString(lineIterator);
        var value = basicInMemoryKeyValueStore.get(key);
        if (nonNull(value)) {
            var response = wrap(decorateBulkString(value));
            socketChannel.write(response);
        } else {
            socketChannel.write(wrap("$-1\r\n".getBytes()));
        }
    }

    private String extractBulkString(Iterator<String> lineIterator) {
        //skip bulk string size
        lineIterator.next();
        return lineIterator.next();
    }
}
