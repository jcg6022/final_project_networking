package ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Connection implements Runnable {
    private static final Logger LOG = Logger.getLogger(Connection.class.getCanonicalName());
    private final WebSocketServer _server;
    private final Socket _clientSocket;
    private final PrintWriter _writer;

    public Connection(WebSocketServer server, Socket clientSocket) throws IOException {
        _server = Objects.requireNonNull(server);
        _clientSocket = Objects.requireNonNull(clientSocket);
        _writer = new PrintWriter(_clientSocket.getOutputStream());
    }

    public void sendMessage(String message) {
        _writer.println(message + "\r\n");
        _writer.flush();
    }

    public void buildHandshake(OutputStream ostream, String data) throws IOException {
        Matcher get = Pattern.compile("^GET").matcher(data);

        if (get.find()) {
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
            match.find();
            try {

                byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n" + "Connection: Upgrade\r\n"
                        + "Upgrade: websocket\r\n" + "Sec-WebSocket-Accept: "
                        + Base64.getEncoder()
                                .encodeToString(MessageDigest.getInstance("SHA-1").digest(
                                        (match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                        + "\r\n\r\n").getBytes("UTF-8");

                ostream.write(response, 0, response.length);
            } catch (NoSuchAlgorithmException e) {
                LOG.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }
    }

    @Override
    public void run() {
        try (InputStream istream = _clientSocket.getInputStream();
                OutputStream ostream = _clientSocket.getOutputStream();
                Scanner scanner = new Scanner(istream, "UTF-8")) {
            System.out.println("Connected: " + _clientSocket.getRemoteSocketAddress());

            String data = scanner.useDelimiter("\\r\\n\\r\\n").next();
            buildHandshake(ostream, data);

            String message;
            while (scanner.hasNextLine()) {
                message = scanner.nextLine();
                System.out.println("Message: " + message);
                _server.broadcast(this, message);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage(), e);
            _server.removeConnection(this);
        }
        _server.removeConnection(this);
    }

}
