import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatClient {
    private static final int PORT = 6379;
    private boolean alive = false;
    private BufferedReader reader;
    private PrintWriter writer;
    private Socket clientSocket;

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.connect();
    }

    private void connect() {
        //Socket clientSocket;
        try {
            clientSocket = new Socket(InetAddress.getLocalHost(),PORT);
            alive = true;
            while(alive){
                writer = new PrintWriter(clientSocket.getOutputStream());
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                //maybe need to implement threads to write and read things in....
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
