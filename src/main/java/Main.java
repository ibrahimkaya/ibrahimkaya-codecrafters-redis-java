import event.EventLoop;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        final Logger logger = Logger.getLogger(EventLoop.class);
        int port = 6379;
        WebServer webServer = null;
        try {
            webServer = new WebServer(port);
            webServer.run();
        } catch (IOException e) {
            logger.info("Web server io exception: ", e);
        } finally {
            try {
                if (webServer != null) {
                    webServer.close();
                }
            } catch (IOException e) {
                logger.info("Web server io exception: ", e);
            }
        }
    }
}
