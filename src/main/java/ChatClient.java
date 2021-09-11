import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ChatClient {
    private static final int PORT = 6379;
    private boolean alive = false;
    private Socket clientSocket;

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.connect();
    }

    private void connect() {
        //Socket clientSocket;
        try {
            clientSocket = new Socket(InetAddress.getLocalHost(),PORT);
            System.out.println("Connected to the chat server");
            alive = true;
            while(alive){
                Connection connection = new ChatClient.Connection(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Connection extends Thread {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private boolean connectionAlive = false;

        private List<String> owner_rooms;
        private String identity;
        private String currentroom;

        public Connection(Socket socket) throws IOException {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream());
        }

        private void requestIdentity(Object jsonObj){

        }

        private void handleIdentity(Object jsonObj){

        }

        @Override
        public void run() {
            connectionAlive = true;
            while (connectionAlive) {
                try {
                    String in = reader.readLine();
                    if (in != null) {
                        //handle the input
                    } else {
                        connectionAlive = false;
                    }
                } catch (IOException e) {
                    connectionAlive = false;
                }
            }
            close();
        }

        public void close() {
            try {
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
