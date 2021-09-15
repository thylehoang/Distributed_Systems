package Server.Commands;

import Server.ClientMeta;
import Server.PoolServer;
import Server.Room;
import com.google.gson.Gson;

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
            for (ClientMeta user : requestedRoom.getConnectedUsers()) {
                // pretend we received a RoomChange from each user -- call the JoinCommand on behalf of each user
                JoinCommand joinCommand = new JoinCommand(this.poolServer, user, "MainHall");
                joinCommand.execute();
            }

            // delete the room
            this.poolServer.removeFromRooms(requestedRoom);
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
