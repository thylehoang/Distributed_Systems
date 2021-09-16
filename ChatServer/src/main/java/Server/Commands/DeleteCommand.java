package Server.Commands;

import Message.S2C.RoomList;
import Server.ClientMeta;
import Server.PoolServer;
import Server.Room;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.HashSet;

public class DeleteCommand extends Command {
    private PoolServer poolServer;
    private ClientMeta user;
    private String roomId;
    public static Gson gson = new Gson();

    public DeleteCommand(PoolServer poolServer, ClientMeta user, String roomId) {
        this.poolServer = poolServer;
        this.user = user;
        this.roomId = roomId;
    }

    @Override
    public void execute() {
        if (checkValid()) {
            Room requestedRoom = getRequestedRoom();
            // create a copy to avoid concurrent modification exceptions
            HashSet<ClientMeta> usersToMove = new HashSet<ClientMeta>(requestedRoom.getConnectedUsers());
            for (ClientMeta user : usersToMove) {
                // pretend we received a RoomChange from each user -- call the JoinCommand on behalf of each user
                JoinCommand joinCommand = new JoinCommand(this.poolServer, user, "MainHall");
                joinCommand.execute();
            }

            // delete the room
            this.poolServer.removeFromRooms(requestedRoom);

            // send RoomList to client that deleted the room
            RoomList roomList = new RoomList(this.poolServer.getRoomLists());
            this.poolServer.sendMessage(gson.toJson(roomList), this.user);
        }
        else {
            // send RoomList to client w/ failed room deletion id + -2 people (impossible!)
            HashMap<String,Integer> errorRoomMap = new HashMap<>();
            errorRoomMap.put(this.roomId, -2);

            RoomList roomList = new RoomList(errorRoomMap);
            this.poolServer.sendMessage(gson.toJson(roomList), this.user);
        }
    }

    @Override
    public boolean checkValid() {
        // check if requested room id exists
        Room requestedRoom = getRequestedRoom();
        if (requestedRoom != null) {
            // check if requested room's owner matches the one putting in the command
            return requestedRoom.getOwner().equals(this.user);
        }
        return false;
    }

    public Room getRequestedRoom() {
        for (Room room : this.poolServer.getRooms()) {
            if (room.getRoomId().equals(this.roomId)) {
                return room;
            }
        }
        return null;
    }

}
