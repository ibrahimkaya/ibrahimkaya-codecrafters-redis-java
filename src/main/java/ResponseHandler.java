import org.apache.log4j.Logger;
import parser.Serialization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public class ResponseHandler {
    private static final Logger logger = Logger.getLogger(ResponseHandler.class);

    public void handle(BufferedReader reader, OutputStream outputStream) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("*")) {
                logger.info("Parsing array, skipping...");
            } else if (line.startsWith("$")) {
                logger.info("Parsing bulk string");
                handleString(reader.readLine(), outputStream);
            } else if (line.startsWith("+")) {
                logger.info("Parsing string");
            }
        }
    }

    private void handleString(String input, OutputStream outputStream) throws IOException {
        if (input.contains("PING")) {
            logger.info("writing PING, input: " + input);
            outputStream.write(Serialization.decorateString("PONG"));
        }
    }
}
