package handler;

import org.apache.log4j.Logger;
import parser.Serialization;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;

public class ResponseHandler {
    private static final Logger logger = Logger.getLogger(ResponseHandler.class);

    public void handle(ByteBuffer readByte, WritableByteChannel socketChannel) throws IOException {
        Iterator<String> lineIterator = new String(readByte.array()).lines().iterator();
        while (lineIterator.hasNext()) {
            lineHandler(socketChannel, lineIterator);
        }
    }

    private void lineHandler(WritableByteChannel socketChannel, Iterator<String> lineIterator) throws IOException {
        var line = lineIterator.next();
        logger.info("readed line: " + line);
        if (line.startsWith("*")) {
            logger.info("Parsing array, skipping...");
        } else if (line.startsWith("$")) {
            logger.info("Parsing bulk string");
            handleString(lineIterator.next(), socketChannel, lineIterator);
        } else if (line.startsWith("+")) {
            logger.info("Parsing string");
        }
    }

    private void handleString(String input, WritableByteChannel socketChannel, Iterator<String> lineIterator) throws IOException {
        if (input.equalsIgnoreCase("PING")) {
            socketChannel.write(ByteBuffer.wrap(Serialization.decorateString("PONG")));
        } else if (input.equalsIgnoreCase("ECHO")) {
            //first next line is bulk string size, so we need to skip it
            logger.info("echoed string size: " + lineIterator.next());
            socketChannel.write(ByteBuffer.wrap(Serialization.decorateBulkString(lineIterator.next())));
        }
    }
}
