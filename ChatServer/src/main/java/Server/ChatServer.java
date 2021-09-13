package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



// support leaving/joining without causing an exception
public class ChatServer {
    private boolean alive = false;
    public static final int PORT = 4444;
//    private List<ChatConnection> connectionList = new ArrayList<>();
    //need dictionary of rooms


    //main functions
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.handle();
    }

    //leave the chat
//    private void leave(ChatConnection conn) {
//        synchronized (connectionList) {
//            connectionList.remove(conn);
//        }
//        broadcast(String.format("%d has left the chat\n", conn.socket.getPort()));
//    }
//
//    //join the chat
//    private void join(ChatConnection conn) {
//        synchronized (connectionList) {
//            connectionList.add(conn);
//        }
//        broadcast(String.format("%d has joined the chat\n", conn.socket.getPort()));
//    }
//
//    //broadcast the message
//    private void broadcast(String message) {
//        synchronized (connectionList) {
//            for (ChatConnection c : connectionList) {
//                    c.sendMessage(message);
//            }
//        }
//    }

    //create newidentity


    //handle connection
    private void handle() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.printf("listening on port %d\n", PORT);
            alive = true;
            while (alive) {
                Socket socket = serverSocket.accept();
                ChatConnection connection = new ChatConnection(socket);
                connection.start();
                if (connection != null) {
                    System.out.printf("Accepted new connection from %s:%d\n", socket.getLocalAddress().getCanonicalHostName(), socket.getPort());
                    connection.run();
                }
//                join(connection);
                else {
                    alive = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            alive = false;
        }
    }

    //chat connection
    private class ChatConnection extends Thread {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private boolean connectionAlive = false;
        private Task task = null;
        private List<String> owner_of;
        private String identity;

        public ChatConnection(Socket socket) throws IOException {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream());
        }

        @Override
        public void run() {
            System.out.printf("Connection run called\n");
            connectionAlive = true;
            while (connectionAlive) {
                try {
                    String in = reader.readLine();
                    if (in != null) {
                        //unmarshall the json
                        //based on type, call the
//                        broadcast(String.format("%d: %s\n", socket.getPort(), in));
                        System.out.printf("%d: %s\n", socket.getPort(), in);
                    } else {
                        connectionAlive = false;
                    }
                } catch (IOException e) {
                    connectionAlive = false;
                    e.printStackTrace();
                }
            }
            System.out.printf("Connection closed\n");
            close();
        }

        public void close() {
            try {
//                leave(this);
                socket.close();
                reader.close();
                writer.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        public void sendMessage(String message) {
            writer.print(message);
            writer.flush();
        }
    }
}