package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.ChatClient;

/**
 * This thread is responsible for reading server's input and printing it to the
 * console. It runs in an infinite loop until the client disconnects from the
 * server.
 *
 */
public class ReadThread extends Thread {
    private static final Logger LOG = Logger.getLogger(ReadThread.class.getCanonicalName());
    private final Socket _socket;
    private final ChatClient _client;

    public ReadThread(Socket socket, ChatClient client) {
        _socket = socket;
        _client = client;
    }

    @Override()
    public void run() {
        while (true) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(_socket.getInputStream()))) {
                String response = reader.readLine();
                System.out.println("\n" + response);

                // prints the username after displaying the server's message
                if (_client.getUserName() != null) {
                    System.out.print("[" + _client.getUserName() + "]: ");
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error reading from server: " + ex.getLocalizedMessage(), ex);
                break;
            }
        }
    }
}