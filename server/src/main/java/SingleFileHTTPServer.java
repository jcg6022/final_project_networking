import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingleFileHTTPServer {
    private static final Logger logger = Logger.getLogger("SingleFileHTTPServer");

    private final int port;
    private final String encoding;
    private final byte[] header;
    private final byte[] data;

    public SingleFileHTTPServer(int port, String encoding, String mimeType, byte[] data) {
        this.port = port;
        this.encoding = encoding;
        this.data = data;

        var sb = new StringBuilder(512);
        sb.append("HTTP/1.0 200 OK\r\n")
          .append("Server: OneFile 2.0\r\n")
          .append(String.format("Content-Length: %s\r\n", this.data.length))
          .append(String.format("Content-Type: %s; charset=UTF-8\r\n\r\n", encoding));

        this.header = sb.toString()
                        .getBytes(StandardCharsets.US_ASCII);
    }

    public SingleFileHTTPServer(int port, String encoding, String mimeType, String data)
            throws UnsupportedEncodingException {
        this(port, encoding, mimeType, data.getBytes(encoding));
    }

    public void start() {
        var pool = Executors.newFixedThreadPool(100);
        try (var server = new ServerSocket(this.port)) {
            logger.info(String.format("Accepting connections on port %s", this.port));
            logger.info("Data to be sent:");
            logger.info(new String(this.data, this.encoding));

            while (true) {
                try (var connection = server.accept()) {
                    pool.submit(new HTTPHandler(connection, header, data));
                } catch (IOException ex) {
                    logger.log(Level.WARNING, "Exception accepting connection", ex);
                } catch (RuntimeException ex) {
                    logger.log(Level.SEVERE, "Unexpected error", ex);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
