package Server.Commands;

import Message.S2C.RoomChange;
import Message.S2C.RoomContents;
import Message.S2C.RoomList;
import Server.ClientMeta;
import Server.PoolServer;
import Server.Room;
import com.google.gson.Gson;

public class JoinCommand extends Command {
    private PoolServer poolServer;
    private ClientMeta user;
    private String newRoomId;
    private Room currentRoom;
    public static Gson gson = new Gson();

    public JoinCommand(PoolServer poolServer, ClientMeta user, String newRoomId) {
        this.poolServer = poolServer;
        this.user = user;
        this.currentRoom = this.user.getConnectedRoom();
        this.newRoomId = newRoomId;
    }

    @Override
    public void execute() {
        if (checkValid()) {
            // get requested room
            Room requestedRoom = getRequestedRoom(this.newRoomId);

            // change client room to requested room
            this.user.setConnectedRoom(requestedRoom);

            // update current/requested room's user lists
            this.currentRoom.removeFromConnectedUsers(this.user);
            requestedRoom.addToConnectedUsers(this.user);

            // send RoomChange message to all clients in the current room and the new room
            RoomChange roomChange = new RoomChange(this.user.getName(), this.currentRoom.getRoomId(), this.newRoomId);
            this.poolServer.broadcastMessage(gson.toJson(roomChange), this.currentRoom);
            this.poolServer.broadcastMessage(gson.toJson(roomChange), requestedRoom);

            // check if room that we moved out of is not mainhall, has empty owner, and is now empty; if so, then we delete the room
            if (!this.currentRoom.getRoomId().equals("MainHall") &&
                    this.currentRoom.getOwner().getName().equals("") &&
                    this.currentRoom.getConnectedUsers().size() == 0) {
                this.poolServer.removeFromRooms(this.currentRoom);
            }

            // if new room is MainHall, then also send RoomContents (for MainHall) and RoomList to client
            if (this.newRoomId.equals("MainHall")) {
                RoomContents roomContents = new RoomContents("MainHall", "", requestedRoom.getConnectedUserNames());
                RoomList roomList = new RoomList(this.poolServer.getRoomLists());

                this.poolServer.sendMessage(gson.toJson(roomContents), this.user);
                this.poolServer.sendMessage(gson.toJson(roomList), this.user);
            }

        }
        else {
            // send RoomChange message to client (former = roomid)
            RoomChange roomChange = new RoomChange(this.user.getName(), this.currentRoom.getRoomId(), this.currentRoom.getRoomId());
            this.poolServer.sendMessage(gson.toJson(roomChange), this.user);
        }
    }

    @Override
    public boolean checkValid() {
        // check if the room name matches any existing rooms
        return getRequestedRoom(this.newRoomId) != null;
    }

    public Room getRequestedRoom(String newRoomId) {
        for (Room room : this.poolServer.getRooms()) {
            if (room.getRoomId().equals(this.newRoomId)) {
                return room;
            }
        }
        return null;
    }

}
