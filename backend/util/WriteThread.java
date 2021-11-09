package util;

import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.ChatClient;

/**
 * This thread is responsible for reading user's input and send it to the
 * server. It runs in an infinite loop until the user types 'bye' to quit.
 *
 */
public class WriteThread extends Thread {
    private static final Logger LOG = Logger.getLogger(WriteThread.class.getCanonicalName());
    private final Socket _socket;
    private final ChatClient _client;

    public WriteThread(Socket socket, ChatClient client) {
        _socket = socket;
        _client = client;
    }

    @Override()
    public void run() {
        try (PrintWriter writer = new PrintWriter(_socket.getOutputStream(), true)) {
            Console console = System.console();
            String userName = console.readLine("\nEnter your name: ");
            _client.setUserName(userName);
            writer.println(userName);

            String text;

            do {
                text = console.readLine("[" + userName + "]: ");
                writer.println(text);
            } while (!text.equals("bye"));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error reading from server: " + ex.getLocalizedMessage(), ex);
        }
    }
}