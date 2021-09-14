package Server.Commands;

import Message.RoomChange;
import Server.ClientMeta;
import Server.PoolServer;
import Server.Room;
import com.google.gson.Gson;

public class QuitCommand extends Command {
    private PoolServer poolServer;
    private ClientMeta user;
    public static Gson gson = new Gson();

    public QuitCommand(PoolServer poolServer, ClientMeta user) {
        this.poolServer = poolServer;
        this.user = user;
    }

    @Override
    public void execute() {
        if (checkValid()) {
            // remove user from current room
            this.user.getConnectedRoom().removeFromConnectedUsers(this.user);

            // send RoomChange message to all clients in that room with roomid = ""
            RoomChange roomChange = new RoomChange(this.user.getName(), this.user.getConnectedRoom().getRoomId(), "");
            this.poolServer.broadcastMessage(gson.toJson(roomChange), this.user.getConnectedRoom());

            // rooms owned by user set to empty owner
            for (Room room : this.user.getOwnedRooms()) {
                room.setOwner(new ClientMeta());
            }

            // close connection from server side
            this.user.getSocketConnection().close();
        }
    }

    @Override
    public boolean checkValid() {
        return true;
    }

}
