package Server;

import java.util.HashSet;

public class Room {
    /*
      Stores information about:
      - owner
      - currently connected users
      - room id
     */
    private ClientMeta owner;
    private HashSet<ClientMeta> connectedUsers;
    private String roomId;

    public Room(String roomId, ClientMeta owner) {
        this.owner = owner;
        this.roomId = roomId;
        this.connectedUsers = new HashSet<>();
    }

    /*
     Setters and Getters
     */
    public String getRoomId() {
        return roomId;
    }

    public ClientMeta getOwner() {
        return owner;
    }

    public HashSet<ClientMeta> getConnectedUsers() {
        return connectedUsers;
    }

    public void addToConnectedUsers(ClientMeta newUser) {
        this.connectedUsers.add(newUser);
    }

    public void setConnectedUsers(HashSet<ClientMeta> connectedUsers) {
        this.connectedUsers = connectedUsers;
    }

    public void setOwner(ClientMeta owner) {
        this.owner = owner;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
