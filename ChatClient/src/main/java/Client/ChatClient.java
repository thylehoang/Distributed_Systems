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

    private String host;
    private int port;

    private String name = "";
    private String roomId = "";

    public ChatClient(String host, int port) {
        this.writer = new PrintWriter(System.out);
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        // parse arguments (should be <hostname> [-p port])
        if (args.length > 0) {
            String host = args[0];
            int port = 4444;
            if (args.length == 3) {
                if (args[1].equals("-p")) {
                    port = Integer.parseInt(args[2]);
                }
            }

            ChatClient chatClient = new ChatClient(host, port);
            chatClient.handleConnection();
        }
        else {
            System.out.println("Please specify hostname!\n");
        }
    }

    private void handleConnection() {
        try {
            Socket socket = new Socket(this.host, this.port);
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
