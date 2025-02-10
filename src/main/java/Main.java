import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int port = 6379;
        WebServer webServer = null;
        try {
            webServer = new WebServer(port);
            webServer.run();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (webServer != null) {
                    webServer.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
}
