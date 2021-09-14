package Server;

import Connection.SocketConnection;
import Message.Message;
import Server.Commands.*;
import com.google.gson.Gson;

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
                // check if it's a command (first character will be #)
                if (line.charAt(0) == '#') {
                    // figure out which command it is, check if its valid (?), then execute the command if validly composed
                    Command command = null;

                    String[] inputs = line.split("\\s+");
                    if (inputs[0].equalsIgnoreCase("#identitychange")) {
                        command = new IDChangeCommand(this.poolServer, user, inputs[1]);
                    }
                    else if (inputs[0].equalsIgnoreCase("#join")) {
                        command = new JoinCommand(this.poolServer, user, inputs[1]);
                    }
                    else if (inputs[0].equalsIgnoreCase("#who")) {
                        command = new WhoCommand(this.poolServer, user);
                    }
                    else if (inputs[0].equalsIgnoreCase("#list")) {
                        command = new ListCommand(this.poolServer, user);
                    }
                    else if (inputs[0].equalsIgnoreCase("#createroom")) {
                        command = new CreateRoomCommand(this.poolServer, user, inputs[1]);
                    }
                    else if (inputs[0].equalsIgnoreCase("#delete")) {
                        command = new DeleteCommand(this.poolServer, user, inputs[1]);
                    }
                    else if (inputs[0].equalsIgnoreCase("quit")) {
                        command = new QuitCommand(this.poolServer, user);
                    }
//                else {
//                    // not a valid command!
//                }

                    if (command != null) {
                        command.execute();
                    }
                }
                // does not start with # = not a command so treat as message
                else {
                    Message message = new Message(user.getName(), line);
                    this.poolServer.broadcastMessage(gson.toJson(message), user.getConnectedRoom());
                }
            }
//        socketConnection.getWriter().println(line);
//        socketConnection.getWriter().flush();
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
