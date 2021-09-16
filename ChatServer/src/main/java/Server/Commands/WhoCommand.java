package Server.Commands;

import Message.S2C.RoomContents;
import Server.ClientMeta;
import Server.PoolServer;
import Server.Room;
import com.google.gson.Gson;

import java.util.ArrayList;

public class WhoCommand extends Command {
    private PoolServer poolServer;
    private ClientMeta user;
    private String roomId;
    public static Gson gson = new Gson();

    public WhoCommand(PoolServer poolServer, ClientMeta user, String roomId) {
        this.user = user;
        this.poolServer = poolServer;
        this.roomId = roomId;
    }

    @Override
    public void execute() {
        if (checkValid()) {
            Room requestedRoom = getRequestedRoom();

            String owner = requestedRoom.getOwner().getName();
            RoomContents roomContents = new RoomContents(this.roomId, owner, requestedRoom.getConnectedUserNames());
            poolServer.sendMessage(gson.toJson(roomContents), user);
        }
        else {
            // room not found; send a RoomContents message with no owner and no-one connected (impossible situation
            // except for MainHall)
            RoomContents roomContents = new RoomContents(this.roomId, "", new ArrayList<String>());
            poolServer.sendMessage(gson.toJson(roomContents), user);
        }
    }

    @Override
    public boolean checkValid() {
        // check if the room name matches any existing rooms
        Room requestedRoom = getRequestedRoom();

        // if name not found, then invalid!
        return requestedRoom != null;
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
