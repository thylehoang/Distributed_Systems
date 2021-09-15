package Client;

import Connection.SocketConnection;
import Message.C2S.*;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputParser implements Runnable{
    /*
     Client.InputParser is used to continually read in client inputs, process them and repackage as json to send to server
     */
    private SocketConnection socketConnection;
    private BufferedReader reader;
    private ChatClient chatClient;
    public static Gson gson = new Gson();

    public InputParser(SocketConnection socketConnection, ChatClient chatClient) {
        this.socketConnection = socketConnection;
        this.chatClient = chatClient;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        while (true) {
            try {
                String input = this.reader.readLine();
                if (input != null) {
//                    System.out.println(input);
                    processInput(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processInput(String input) {
        // check if input is command (starts with #)
        String[] inputs = input.split("\\s+");
        String messageToSend = null;

        if (inputs.length > 0) {
            if (inputs[0].length() > 0) {
                if (inputs[0].charAt(0) == '#') {
                    if (inputs.length == 1) {
                        if (inputs[0].equals("#list")) {
                            List list = new List();
                            messageToSend = gson.toJson(list);
                        }
                        else if (inputs[0].equals("#quit")) {
                            Quit quit = new Quit();
                            messageToSend = gson.toJson(quit);
                        }
                    }
                    else if (inputs.length > 1){
                        if (inputs[0].equals("#identitychange")) {
                            IdentityChange identityChange = new IdentityChange(inputs[1]);
                            messageToSend = gson.toJson(identityChange);
                        }
                        else if (inputs[0].equals("#join")) {
                            Join join = new Join(inputs[1]);
                            messageToSend = gson.toJson(join);
                        }
                        else if (inputs[0].equals("#who")) {
                            Who who = new Who(inputs[1]);
                            messageToSend = gson.toJson(who);
                        }

                        else if (inputs[0].equals("#createroom")) {
                            CreateRoom createRoom = new CreateRoom(inputs[1]);
                            messageToSend = gson.toJson(createRoom);
                        }
                        else if (inputs[0].equals("#delete")) {
                            Delete delete = new Delete(inputs[1]);
                            messageToSend = gson.toJson(delete);
                        }
                    }
                }
                else {
                    // otherwise it's a message
                    MessageC2S messageC2S = new MessageC2S(input);
                    messageToSend = gson.toJson(messageC2S);
                }
            }

        }


        if (messageToSend != null) {
//            System.out.printf("Message to send: %s\n", messageToSend);
            try {
                this.socketConnection.getBufferedWriter().write(messageToSend);
                this.socketConnection.getBufferedWriter().newLine();
                this.socketConnection.getBufferedWriter().flush();
            } catch (IOException e) {
                // connection likely lost to server. Trying to send another message won't work because the server socket
                // is likely closed. Hence, we exit so that the user can restart the client and reconnect
                System.out.println("Failed to send message to server! Connection likely lost. Aborting.\n");
//                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
