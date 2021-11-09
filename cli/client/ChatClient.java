package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.ReadThread;
import util.WriteThread;

/**
 * This is the chat client program. Type 'bye' to terminte the program.
 */
public class ChatClient {
  private String _hostname;
  private int _port;
  private String _userName;

  public ChatClient(String hostname, int port) {
    _hostname = hostname;
    _port = port;
  }

  public void execute() {
    try {
      Socket socket = new Socket(_hostname, _port);

      System.out.println("Connected to the chat server");

      ExecutorService threadPool = Executors.newFixedThreadPool(2);
      threadPool.submit(new ReadThread(socket, this));
      threadPool.submit(new WriteThread(socket, this));

    } catch (UnknownHostException ex) {
      System.out.println("Server not found: " + ex.getMessage());
    } catch (IOException ex) {
      System.out.println("I/O Error: " + ex.getMessage());
    }

  }

  public void setUserName(String userName) {
    _userName = userName;
  }

  public String getUserName() {
    return _userName;
  }

  public static void main(String[] args) {
    if (args.length < 2)
      return;

    String hostname = args[0];
    int port = Integer.parseInt(args[1]);

    ChatClient client = new ChatClient(hostname, port);
    client.execute();
  }
}
