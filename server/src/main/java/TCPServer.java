import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class TCPServer {
    private static final Logger logger = Logger.getLogger("Server");

    // Constants
    private static final int PATH_IDX = 0;
    private static final int PORT_IDX = 1;
    private static final int ENCODING_IDX = 2;
    private static final int PORT_MIN = 1;
    private static final int PORT_MAX = 65535;
    private static final int PORT_DEFAULT = 80;
    private static final String ENCODING_DEFAULT = "UTF-8";


    public static void main(String[] args) {
        int port = PORT_DEFAULT;

        try {
            port = Integer.parseInt(args[PORT_IDX]);
            if (port < PORT_MIN || port > PORT_MAX) {
                port = PORT_DEFAULT;
            }
        } catch (NumberFormatException e) {
            logger.warning(
                    String.format(
                            "Unable to establish a connection on port %s. Falling back to port %s",
                            args[PORT_IDX],
                            PORT_DEFAULT
                    )
            );
        }

        String encoding = ENCODING_DEFAULT;
        if (args.length > 2) {
            encoding = args[ENCODING_IDX];
        }

        try {
//            var path = Paths.get(args[PATH_IDX]);
//            var data = Files.readAllBytes(path);
            var data = TCPServer.class.getClassLoader().getResourceAsStream("hello.txt");
            if (data == null) {
                throw new Exception("Resource not available");
            }

            var contentType = URLConnection.getFileNameMap().getContentTypeFor(args[PATH_IDX]);
            var server = new SingleFileHTTPServer(port, encoding, contentType, data.readAllBytes());
            server.start();
        } catch (IOException e) {
            logger.severe(e.getMessage());
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }
}
