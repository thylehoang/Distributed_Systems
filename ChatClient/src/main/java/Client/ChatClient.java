package Client;

import Connection.SocketConnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private boolean keepAlive = true;
    private SocketConnection socketConnection;
    private PrintWriter writer;
    private ClientLineProcessor clientLineProcessor;

    private String name = "";
    private String roomId = "";

    public ChatClient() {
        this.writer = new PrintWriter(System.out);
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();

        // TODO: change to CL arguments
        String host = "localhost";
        int port = 4444;

        chatClient.handleConnection(host, port);

    }

    private void handleConnection(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            SocketConnection socketConnection = new SocketConnection(socket);

            // run STDIN reader on separate thread
            InputParser inputParser = new InputParser(socketConnection, this);
            Thread t = new Thread(inputParser);
            t.start();

            // create client line processor to process stdout output
            this.clientLineProcessor = new ClientLineProcessor(socketConnection, this);

            while (keepAlive) {
                if (socketConnection.getReader().ready()) {
                    String in = socketConnection.getReader().readLine();
                    if (in != null) {
//                        System.out.println(in);
                        this.clientLineProcessor.processLine(in);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getName() {
        return name;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public PrintWriter getWriter() {
        return writer;
    }
}
