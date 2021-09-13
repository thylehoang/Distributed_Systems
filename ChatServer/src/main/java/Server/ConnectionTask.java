package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ConnectionTask implements Runnable{
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private boolean connectionAlive = false;

    public ConnectionTask(Socket socket) throws IOException {
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
            socket.close();
            reader.close();
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
