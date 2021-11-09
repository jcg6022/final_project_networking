package ws;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebSocketServer {

    private static final Logger LOG = Logger.getLogger(WebSocketServer.class.getCanonicalName());
    private final Set<Connection> _clients = new HashSet<>();

    public static void main(String[] args) {
        WebSocketServer server = new WebSocketServer();
        server.start();
    }

    public void start() {
        try (ServerSocket server = new ServerSocket(80)) {
            LOG.info("Now listening on http://localhost:80");
            ExecutorService threadPool = Executors.newFixedThreadPool(50);

            try {
                Socket clientSocket = server.accept();
                threadPool.submit(new Connection(this, clientSocket));
            } catch (IOException e) {
                // Client disconnected
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    public void broadcast(Connection excludedConnection, String message) {
        final Set<Connection> connections = _clients;
        for (Connection connection : connections) {
            if (!Objects.equals(connection, excludedConnection)) {
                connection.sendMessage(message);
            }
        }
    }

    public boolean addConnection(Connection connection) {
        return _clients.add(connection);
    }

    public boolean removeConnection(Connection connection) {
        return _clients.remove(connection);
    }
}