package Server.Commands;

import Message.RoomContents;
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
            Room requestedRoom = null;
            // get requested room
            for (Room room : this.poolServer.getRooms()) {
                if (room.getRoomId().equals(this.roomId)) {
                    requestedRoom = room;
                }
            }

            if (requestedRoom != null) {
                String owner = requestedRoom.getOwner().getName();
                RoomContents roomContents = new RoomContents(this.roomId, owner, requestedRoom.getConnectedUserNames());
                poolServer.sendMessage(gson.toJson(roomContents), user);
            }
            else {
                // TODO could not find requested room
            }
        }
    }

    @Override
    public boolean checkValid() {
        return true;
    }

}
