package Server;

import Connection.SocketConnection;
import Message.Message;
import Server.Commands.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LineProcessor implements Runnable {
    private SocketConnection socketConnection;
    private String line;
    private PoolServer poolServer;
    public static Gson gson = new Gson();

    public LineProcessor(SocketConnection socketConnection, String line, PoolServer poolServer) {
        this.socketConnection = socketConnection;
        this.line = line;
        this.poolServer = poolServer;
    }

    @Override
    public void run() {
        System.out.printf("[%s] %d: %s\n", Thread.currentThread().getName(), socketConnection.getSocket().getPort(), line);
        // get current user id
        ClientMeta user = getSocketClientMeta();

        if (user != null) {
            if (line.length() != 0) {
                // read in json message and figure out what type it is
                JsonElement jsonElement = JsonParser.parseString(line);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement typeElement = jsonObject.get("type");
                String type = typeElement.getAsString();

                Command command = null;

                if (type.equals("identitychange")) {
                    String identity = jsonObject.get("identity").getAsString();
                    command = new IDChangeCommand(this.poolServer, user, identity);
                }
                else if (type.equals("join")) {
                    String roomId = jsonObject.get("roomid").getAsString();
                    command = new JoinCommand(this.poolServer, user, roomId);
                }
                else if (type.equals("who")) {
                    String roomId = jsonObject.get("roomid").getAsString();
                    command = new WhoCommand(this.poolServer, user, roomId);
                }
                else if (type.equals("list")) {
                    command = new ListCommand(this.poolServer, user);
                }
                else if (type.equals("createroom")) {
                    String newRoomId = jsonObject.get("roomid").getAsString();
                    command = new CreateRoomCommand(this.poolServer, user, newRoomId);
                }
                else if (type.equals("delete")) {
                    String roomId = jsonObject.get("roomid").getAsString();
                    command = new DeleteCommand(this.poolServer, user, roomId);
                }
                else if (type.equals("quit")) {
                    command = new QuitCommand(this.poolServer, user);
                }
                else if (type.equals("message")) {
                    String messageContent = jsonObject.get("content").getAsString();
                    Message message = new Message(user.getName(), messageContent);
                    this.poolServer.broadcastMessage(gson.toJson(message), user.getConnectedRoom());
                }

                if (command != null) {
                        command.execute();
                }

            }
        }
    }

    private ClientMeta getSocketClientMeta() {
        for (ClientMeta user : this.poolServer.getUsers()) {
            if (user.getSocketConnection() == this.socketConnection) {
                return user;
            }
        }

        return null;
    }
}
