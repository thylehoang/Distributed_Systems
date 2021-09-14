package Server;

import Message.IdentityChange;
import Message.RoomContents;
import Message.RoomList;
import Message.Who;
import Server.Commands.IDChangeCommand;
import Server.Commands.ListCommand;
import Server.Commands.WhoCommand;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PoolServer{
    private boolean alive = false;
    public static final int PORT = 4444;
    private PoolHandler poolHandler;

    private ClientMeta serverUser;
    private Room mainHall;
    private HashSet<ClientMeta> users;
    private HashSet<Room> rooms;
    private HashSet<Integer> defaultIds;

    public PoolServer() {
        this.poolHandler = new PoolHandler(2, this);
        this.users = new HashSet<>();
        this.rooms = new HashSet<>();
        this.defaultIds = new HashSet<>();

        // create and add main hall to rooms (create dummy server user)
        this.serverUser = new ClientMeta();
        this.mainHall = new Room("MainHall", this.serverUser);
        this.rooms.add(mainHall);
    }

    //main functions
    public static void main(String[] args) {
        PoolServer poolServer = new PoolServer();
        // run the pool handler on separate thread
        Thread t = new Thread(poolServer.poolHandler);
        t.start();

        poolServer.handle();
    }

    private void handle() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.printf("listening on port %d\n", PORT);
            alive = true;
            while (alive) {
                // wait until a new client connects (.accept is a blocking method)
                Socket socket = serverSocket.accept();
                System.out.printf("New connection established! Adding to pool handler open sockets\n");
                if (socket != null) {
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
                        if (room.getRoomId() == "MainHall") {
                            room.addToConnectedUsers(newUser);
                        }
                    }

                    // send initial identity change message to new user
                    Gson gson = new Gson();

                    IdentityChange idChange = new IdentityChange("", newUser.getName());
                    sendMessage(gson.toJson(idChange), newUser);

                    // send room contents message to new user
                    RoomContents roomContents = new RoomContents("MainHall", "", this.mainHall.getConnectedUserNames());
                    sendMessage(gson.toJson(roomContents), newUser);

                    // send room list message to new user
                    RoomList roomList = new RoomList(this.getRoomLists());
                    sendMessage(gson.toJson(roomList), newUser);

                    // TODO: send initial room join message to all connected clients (in main hall?)

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

    public void broadcastMessageToAll(String jsonMessage) {
        for (Room room : rooms) {
            broadcastMessage(jsonMessage, room);
        }
    }

    public void broadcastMessage(String jsonMessage, Room room) {
        HashSet<ClientMeta> connectedUsers = room.getConnectedUsers();
        for (ClientMeta user : connectedUsers) {
            sendMessage(jsonMessage, user);
        }
    }

    public void sendMessage(String jsonMessage, ClientMeta user) {
        if (user.getSocketConnection() != null) {
            user.getSocketConnection().getWriter().print(jsonMessage);
            user.getSocketConnection().getWriter().println();
            user.getSocketConnection().getWriter().flush();
        }
    }

    public HashSet<ClientMeta> getUsers() {
        return users;
    }

    public HashSet<Room> getRooms() {
        return rooms;
    }
}
