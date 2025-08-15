import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 5555;

        if (args.length >= 1) host = args[0];
        if (args.length >= 2) {
            try { port = Integer.parseInt(args[1]); } catch (NumberFormatException ignored) {}
        }

        try (Socket socket = new Socket(host, port)) {
            System.out.println("[CLIENT] Connected to " + host + ":" + port);

            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out     = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            Scanner scanner = new Scanner(System.in);

            // Read server greetings asynchronously
            Thread reader = new Thread(() -> {
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    // stream closed
                } finally {
                    System.out.println("[CLIENT] Disconnected.");
                    System.exit(0);
                }
            }, "ServerReader");
            reader.setDaemon(true);
            reader.start();

            // Prompt for nickname once connected
            System.out.print("Enter your nickname (3–16 chars, letters/numbers/_): ");
            String nick = scanner.nextLine().trim();
            if (!nick.isEmpty()) {
                out.println("/nick " + nick);
            }

            System.out.println("Type messages to chat. Commands: /w <user> <msg>, /list, /quit");

            // Main send loop
            while (true) {
                if (!scanner.hasNextLine()) break;
                String msg = scanner.nextLine();
                out.println(msg);
                if (msg.trim().equalsIgnoreCase("/quit")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("[CLIENT] Error: " + e.getMessage());
        }
    }
}
