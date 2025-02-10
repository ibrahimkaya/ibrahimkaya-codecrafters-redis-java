package handler;

import org.apache.log4j.Logger;
import parser.Serialization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ResponseHandler {
    private static final Logger logger = Logger.getLogger(ResponseHandler.class);

    public void handle(ByteBuffer readByte, SocketChannel socketChannel) throws IOException {
        logger.info("starting handle");
        //todo test ortamında okey, localde new lineları kaybediyoruz ? bu türün çalışacağına emin olduğun bir yöntem bulman gerekli
        // ayrıca unit test input output üzerinden çalışacak şekilde değiştirmelisin, full case test edersen localde tetiklemeye pek gerek olmaz

        var lineIterator = new String(readByte.array()).lines().iterator();
        while (lineIterator.hasNext()) {
            var line = lineIterator.next();
            logger.info("readed line: " + line);
            if (line.startsWith("*")) {
                logger.info("Parsing array, skipping...");
            } else if (line.startsWith("$")) {
                logger.info("Parsing bulk string");
                handleString(lineIterator.next(), socketChannel);
            } else if (line.startsWith("+")) {
                logger.info("Parsing string");
            }
        }
    }

    public void handle(BufferedReader reader, OutputStream outputStream) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("*")) {
                logger.info("Parsing array, skipping...");
            } else if (line.startsWith("$")) {
                logger.info("Parsing bulk string");
                //   handleString(reader.readLine(), outputStream);
            } else if (line.startsWith("+")) {
                logger.info("Parsing string");
            }
        }
    }

    private void handleString(String input, SocketChannel socketChannel) throws IOException {
        if (input.contains("PING")) {
            logger.info("writing PING, input: " + input);
            socketChannel.write(ByteBuffer.wrap(Serialization.decorateString("PONG")));
        }
    }
}
