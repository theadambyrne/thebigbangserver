package CS4442.OS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import CS4442.OS.Command.ServerSignals;

public class Server implements Runnable {
    private ArrayList<ClientHandler> clients = new ArrayList<>();
    private Logger logger = Logger.getLogger(Server.class.getName());
    private ServerSocket serverSocket;
    private boolean running = true;

    public static void main(String[] args) {
        Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();

        try {
            serverThread.join();
        } catch (InterruptedException e) {
            serverThread.interrupt();
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(1234);

            logger.info("Server started");

            ExecutorService pool = Executors.newCachedThreadPool(); // will reuse old threads
            while (running) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                pool.execute(clientHandler);
            }

            pool.shutdown();
            serverSocket.close();
            logger.info("Server stopped");

        } catch (IOException e) {
            logger.warning("Server failed to start");
            System.exit(1);
        }

    }

    public void shutdown() {
        logger.info("Server shutting down");

        try {
            running = false;
            broadcast(new Message("Server", "shutting down"));

            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }

            for (ClientHandler client : clients) {
                client.shutdown();
            }

        } catch (IOException e) {
            logger.warning("Server shutdown failed");
            e.printStackTrace();
        }
    }

    public void broadcast(Message message) {
        for (ClientHandler client : clients) {
            client.send(message);
        }
    }

    public class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println(
                        new Message("Server", "Welcome to THE BIG BANG SERVER. Type /help for a list of commands"));
                out.println(new Message("Server", "Enter a nickname:"));

                nickname = in.readLine();
                if (nickname == null || nickname.equals("")) {
                    out.println(new Message("Server", "Invalid nickname"));
                    shutdown();
                    return;
                }

                broadcast(new Message("Server", "Welcome to the chat, " + nickname));
                System.out.println(new Message("Server", nickname + " has joined the chat"));

                String message;
                while ((message = in.readLine()) != null) {
                    Message msg = new Message(nickname, message);

                    if (msg.validate()) {
                        if (msg.getBody().charAt(0) == '/') {
                            handleCommand(msg, nickname);
                        } else {
                            broadcast(msg);
                        }

                        System.out.println(msg);

                    } else {
                        out.println(new Message("Server", "Invalid message"));
                    }
                }

                shutdown();

            } catch (Exception e) {
                Message goodbyeMsg = new Message("Server", nickname + " has left the chat");
                clients.remove(this);
                broadcast(goodbyeMsg);
                out.println(goodbyeMsg);
                System.out.println(goodbyeMsg);
            }
        }

        private void handleCommand(Message msg, String nickname) {
            try {
                Command command = new Command(msg.getBody().substring(1));
                ServerSignals signal = command.execute(out);

                switch (signal) {
                    case HELP:
                        out.println(new Message("Server", "Available commands:"));
                        out.println(new Message("Server", "/help - display this message"));
                        out.println(new Message("Server", "/quit - quit the chat"));
                        out.println(new Message("Server", "/clear - clear the chat"));
                        out.println(new Message("Server", "/list - list all online users"));
                        out.println(new Message("Server", "/panic - clear the chat for everyone"));
                        break;

                    case QUIT:
                        out.println(new Message("Server", nickname + " has left the chat"));
                        shutdown();
                        break;

                    case CLEAR:
                        out.println(new Message("Server", "\033[H\033[2J"));
                        out.println(new Message("Server", "your chat is cleared"));
                        break;

                    case LIST:
                        String[] names = new String[clients.size()];
                        for (int i = 0; i < clients.size(); i++) {
                            names[i] = clients.get(i).nickname;
                        }
                        out.println(new Message("Server", clients.size() + " online - " + String.join(", ", names)));
                        break;

                    case PANIC:
                        broadcast(new Message("Server", "\033[H\033[2J"));
                        break;

                    default:
                        out.println(new Message("Server", "Invalid command"));
                        break;
                }

            } catch (IllegalArgumentException e) {
                out.println(new Message("Server", "Invalid command"));
            }
        }

        public void send(Message message) {
            out.println(message);
        }

        public void shutdown() {
            clients.remove(this);
            try {
                in.close();
                out.close();

                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                logger.warning("Client shutdown failed");
                e.printStackTrace();
            }
        }

    }

}
