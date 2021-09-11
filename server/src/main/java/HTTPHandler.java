import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HTTPHandler implements Callable<Void> {
    private static final Logger logger = Logger.getLogger("HTTPHandler");
    private final Socket connection;
    private final byte[] header;
    private final byte[] data;

    public HTTPHandler(Socket connection, byte[] header, byte[] data) {
        this.connection = connection;
        this.header = header;
        this.data = data;
    }

    @Override
    public Void call() throws IOException {
        try (
                var out = new BufferedOutputStream(connection.getOutputStream());
                var in = new BufferedInputStream(connection.getInputStream());
        ) {
            // Read the first line only; that is all we need.
            var request = new StringBuilder(128);

            // Build the request string until:
            // "carriage return" (\r) or a "new line feed" (\n).
            while (true) {
                var c = in.read();
                if (c == '\r' || c == '\n' || c == -1) {
                    break;
                }

                request.append((char) c);
            }

            // If this is HTTP/1.0 or later, send MIME header
            if (request.toString().contains("HTTP/")) {
                out.write(this.header);
            }

            out.write(this.data);
            out.flush();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing to client");
        }

        return null;
    }
}
