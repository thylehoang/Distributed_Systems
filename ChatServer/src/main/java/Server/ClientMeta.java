package Server;

import Connection.SocketConnection;

import java.util.HashSet;

public class ClientMeta {
    /*
      Stores information about client:
      - current name
      - current connected room
      - owned rooms
     */
    private String name;
    private Room connectedRoom;
    private HashSet<Room> ownedRooms;
    private SocketConnection socketConnection;

    public ClientMeta() {
        this.name = "";
        this.connectedRoom = null;
        this.socketConnection = null;
        this.ownedRooms = new HashSet<>();
    }

    public ClientMeta(int id, Room connectedRoom, SocketConnection socketConnection) {
        this.name = String.format("guest%d", id);
        this.connectedRoom = connectedRoom;
        this.ownedRooms = new HashSet<>();
        this.socketConnection = socketConnection;
    }

    /*
     Setters and Getters
     */
    public HashSet<Room> getOwnedRooms() {
        return ownedRooms;
    }

    public Room getConnectedRoom() {
        return connectedRoom;
    }

    public String getName() {
        return name;
    }

    public SocketConnection getSocketConnection() {
        return socketConnection;
    }

    public void setConnectedRoom(Room connectedRoom) {
        this.connectedRoom = connectedRoom;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnedRooms(HashSet<Room> ownedRooms) {
        this.ownedRooms = ownedRooms;
    }

    public void setSocketConnection(SocketConnection socketConnection) {
        this.socketConnection = socketConnection;
    }
}
