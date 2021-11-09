package server;

import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.ServerSocket;
import java.io.IOException;

/**
 * This is the chat server program. Press Ctrl + C to terminate the program.
 *
 */
public class ChatServer {
    private final int _port;
    private final Set<String> userNames = new HashSet<>();
    private final Set<UserThread> userThreads = new HashSet<>();

    public ChatServer(int port) {
        _port = port;
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(_port)) {
            ExecutorService threadPool = Executors.newFixedThreadPool(50);
            System.out.println("Chat Server is listening on port " + _port);

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("New user connected");

                    UserThread newUserThread = new UserThread(socket, this);
                    userThreads.add(newUserThread);
                    threadPool.submit(newUserThread);
                } catch (IOException ex) {
                    // client disconnected
                }
            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // if (args.length < 1) {
        // System.out.println("Syntax: java ChatServer <port-number>");
        // System.exit(0);
        // }

        int port = args.length == 1 ? Integer.parseInt(args[0]) : 8000;

        ChatServer server = new ChatServer(port);
        server.execute();
    }

    /**
     * Delivers a message from one user to others (broadcasting)
     */
    void broadcast(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    /**
     * Stores username of the newly connected client.
     */
    void addUserName(String userName) {
        userNames.add(userName);
    }

    /**
     * When a client is disconneted, removes the associated username and UserThread
     */
    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    /**
     * Returns true if there are other users connected (not count the currently
     * connected user)
     */
    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
}