package Connection;

import java.io.*;
import java.net.Socket;

public class SocketConnection {
    private final Socket socket;
    private BufferedReader reader;
    private BufferedWriter bufferedWriter;

    public SocketConnection(Socket socket) {
        this.socket = socket;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public BufferedReader getReader() {
        return reader;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public Socket getSocket() {
        return socket;
    }

    public void close() {
        try {
            this.reader.close();
            this.bufferedWriter.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


