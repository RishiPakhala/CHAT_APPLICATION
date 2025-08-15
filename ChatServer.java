import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {

    private final int port;
    // username -> ClientHandler
    private final ConcurrentMap<String, ClientHandler> clients = new ConcurrentHashMap<>();
    // to generate temporary guest names
    private final AtomicInteger guestCounter = new AtomicInteger(1);

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("[SERVER] Starting on port " + port + " ...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[SERVER] Listening on " + serverSocket.getLocalPort());
            while (true) {
                Socket socket = serverSocket.accept();
                String tempName = "guest" + guestCounter.getAndIncrement();
                ClientHandler handler = new ClientHandler(tempName, socket);
                clients.put(tempName, handler);
                new Thread(handler, "ClientHandler-" + tempName).start();
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Fatal: " + e.getMessage());
        }
    }

    private void broadcast(String from, String message) {
        String payload = "[ALL][" + from + "]: " + message;
        for (ClientHandler ch : clients.values()) {
            ch.send(payload);
        }
    }

    private void systemBroadcast(String message) {
        String payload = "[SYSTEM]: " + message;
        for (ClientHandler ch : clients.values()) {
            ch.send(payload);
        }
    }

    private void whisper(String from, String to, String message) {
        ClientHandler target = clients.get(to);
        if (target != null) {
            target.send("[WHISPER][" + from + " -> " + to + "]: " + message);
        }
    }

    private Set<String> usernames() {
        return new TreeSet<>(clients.keySet());
    }

    private void renameUser(String oldName, String newName, ClientHandler handler) {
        clients.remove(oldName);
        clients.put(newName, handler);
    }

    private void removeUser(String name) {
        clients.remove(name);
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private volatile String username;
        private BufferedReader in;
        private PrintWriter out;

        ClientHandler(String username, Socket socket) {
            this.username = username;
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                send("Welcome to Java Chat! Your temporary name is '" + username + "'.");
                send("Commands: /nick <name>, /w <user> <msg>, /list, /quit");
                systemBroadcast(username + " joined. (" + socket.getRemoteSocketAddress() + ")");

                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    if (line.startsWith("/")) {
                        handleCommand(line);
                    } else {
                        broadcast(username, line);
                    }
                }
            } catch (IOException e) {
                // connection dropped
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
                removeUser(username);
                systemBroadcast(username + " left.");
            }
        }

        private void handleCommand(String line) {
            String[] parts = line.split("\\s+", 3);
            String cmd = parts[0].toLowerCase(Locale.ROOT);

            switch (cmd) {
                case "/nick": {
                    if (parts.length < 2) {
                        send("Usage: /nick <newName>");
                        return;
                    }
                    String newName = parts[1];
                    if (!newName.matches("[A-Za-z0-9_]{3,16}")) {
                        send("Name must be 3–16 chars: letters, numbers, underscore.");
                        return;
                    }
                    if (clients.containsKey(newName)) {
                        send("Name already taken.");
                        return;
                    }
                    String old = username;
                    renameUser(old, newName, this);
                    username = newName;
                    systemBroadcast(old + " is now known as " + newName + ".");
                    send("You are now '" + newName + "'.");
                    break;
                }
                case "/w": {
                    if (parts.length < 3) {
                        send("Usage: /w <user> <message>");
                        return;
                    }
                    String to = parts[1];
                    String msg = parts[2];
                    if (!clients.containsKey(to)) {
                        send("User '" + to + "' not found.");
                        return;
                    }
                    whisper(username, to, msg);
                    // echo to sender as confirmation
                    send("[WHISPER][you -> " + to + "]: " + msg);
                    break;
                }
                case "/list": {
                    send("Online (" + clients.size() + "): " + String.join(", ", usernames()));
                    break;
                }
                case "/quit": {
                    send("Goodbye!");
                    try { socket.close(); } catch (IOException ignored) {}
                    break;
                }
                default:
                    send("Unknown command. Try /nick, /w, /list, /quit");
            }
        }

        void send(String msg) {
            if (out != null) out.println(msg);
        }
    }

    public static void main(String[] args) {
        int port = 5555;
        if (args.length > 0) {
            try { port = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}
        }
        new ChatServer(port).start();
    }
}
