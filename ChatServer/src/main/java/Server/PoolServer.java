package Server;

import Connection.SocketConnection;
import Message.S2C.NewIdentity;
import Message.S2C.RoomChange;
import Message.S2C.RoomContents;
import Message.S2C.RoomList;
import Server.Commands.QuitCommand;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class PoolServer{
    private boolean alive = false;
    // TODO: make CL argument
//    public static final int PORT = 4444;
    private final int port;
    private final PoolHandler poolHandler;

    private final ClientMeta serverUser;
    private final Room mainHall;
    private final HashSet<ClientMeta> users;
    private final HashSet<Room> rooms;
    private final HashSet<Integer> defaultIds;

    public PoolServer(int port) {
        this.poolHandler = new PoolHandler(2, this);
        this.users = new HashSet<>();
        this.rooms = new HashSet<>();
        this.defaultIds = new HashSet<>();
        this.port = port;

        // create and add main hall to rooms (create dummy server user)
        this.serverUser = new ClientMeta();
        this.mainHall = new Room("MainHall", this.serverUser);
        this.rooms.add(mainHall);
    }

    //main functions
    public static void main(String[] args) {
        // parse arguments (should only be -p port)
        int port = 4444;
        if (args.length == 2) {
            if (args[0].equals("-p")) {
                port = Integer.parseInt(args[1]);
            }
        }

        PoolServer poolServer = new PoolServer(port);
        // run the pool handler on separate thread
        Thread t = new Thread(poolServer.poolHandler);
        t.start();

        poolServer.handle();
    }

    private void handle() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(this.port);
            System.out.printf("listening on port %d\n", this.port);
            alive = true;
            while (alive) {
                // wait until a new client connects (.accept is a blocking method)
                Socket socket = serverSocket.accept();
                if (socket != null) {
                    System.out.printf("New connection [%d] established! Adding to pool handler open sockets\n", socket.getPort());

                    // add socket to socket pool
                    SocketConnection socketConnection = new SocketConnection(socket);
                    poolHandler.addToOpenSockets(socketConnection);

                    // create new ClientMeta for new user, add user to users, add new id to default ids
                    int defaultID = getLowestUnoccupiedId();
                    System.out.printf("Creating new ClientMeta for guest%d\n", defaultID);
                    ClientMeta newUser = new ClientMeta(defaultID, this.mainHall, socketConnection);
                    this.users.add(newUser);
                    this.defaultIds.add(defaultID);

                    // add new user to main hall (can probably make this more efficient)
                    for (Room room : this.rooms) {
                        if (room.getRoomId().equals("MainHall")) {
                            room.addToConnectedUsers(newUser);
                        }
                    }

                    // send initial identity change message to new user
                    Gson gson = new Gson();

                    NewIdentity newIdentity = new NewIdentity("", newUser.getName());
                    sendMessage(gson.toJson(newIdentity), newUser);

                    // send room contents message to new user
                    RoomContents roomContents = new RoomContents("MainHall", "", this.mainHall.getConnectedUserNames());
                    sendMessage(gson.toJson(roomContents), newUser);

                    // send room list message to new user
                    RoomList roomList = new RoomList(this.getRoomLists());
                    sendMessage(gson.toJson(roomList), newUser);

                    // send initial room join message to all connected clients (in main hall?)
                    RoomChange roomChange = new RoomChange(newUser.getName(), "", "MainHall");
                    broadcastMessage(gson.toJson(roomChange), this.mainHall);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Integer> getRoomLists() {
        HashMap<String, Integer> roomList = new HashMap<>();
        for (Room room : this.rooms) {
            roomList.put(room.getRoomId(), room.getConnectedUsers().size());
        }
        return roomList;
    }

    public void addToRooms(Room room) {
        this.rooms.add(room);
    }

    public void removeFromRooms(Room room) {
        this.rooms.remove(room);
    }

    private int getLowestUnoccupiedId() {
        if (this.defaultIds.size() == 0) {
            return 1;
        }
        else {
            int i = 1;
            while (true) {
                if (!this.defaultIds.contains(i)) {
                    return i;
                }
                i = i + 1;
            }
        }
    }

    public void removeFromDefaultIds(int idNumber) {
        this.defaultIds.remove(idNumber);
    }

    public void broadcastMessageToAll(String jsonMessage) {
        for (Room room : rooms) {
            broadcastMessage(jsonMessage, room);
        }
    }

    public void broadcastMessage(String jsonMessage, Room room) {
        HashSet<ClientMeta> connectedUsers = room.getConnectedUsers();
        for (ClientMeta user : connectedUsers) {
//            System.out.printf("Sending message to %s\n", user.getName());
            sendMessage(jsonMessage, user);
        }
    }

    public void sendMessage(String jsonMessage, ClientMeta user) {
        if (user.getSocketConnection() != null && !user.getDisconnected()) {
            try {
                user.getSocketConnection().getBufferedWriter().write(jsonMessage);
                user.getSocketConnection().getBufferedWriter().newLine();
                user.getSocketConnection().getBufferedWriter().flush();
            } catch (IOException e) {
                System.out.printf("Exception when trying to buffer write to socket (user: %s)! Disconnecting socket\n", user.getName());
                disconnectClientSocket(user);
//                e.printStackTrace();
            }
        }
    }

    public HashSet<ClientMeta> getUsers() {
        return users;
    }

    public HashSet<Room> getRooms() {
        return rooms;
    }

    public PoolHandler getPoolHandler() {
        return poolHandler;
    }

    private void disconnectClientSocket(ClientMeta user) {
        /*
         Called when a TCP connection has been detected to have been interrupted (failure to write to buffer).
         */
        user.setDisconnected(true);
        QuitCommand quitCommand = new QuitCommand(this, user);
        quitCommand.execute();

    }
}
