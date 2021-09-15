package Server.Commands;

import Message.S2C.RoomChange;
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
            // send RoomChange message to all clients in that room with roomid = ""
            RoomChange roomChange = new RoomChange(this.user.getName(), this.user.getConnectedRoom().getRoomId(), "");
            this.poolServer.broadcastMessage(gson.toJson(roomChange), this.user.getConnectedRoom());

            // remove user from current room
            this.user.getConnectedRoom().removeFromConnectedUsers(this.user);

            // rooms owned by user set to empty owner
            for (Room room : this.user.getOwnedRooms()) {
                room.setOwner(new ClientMeta());
            }

            // check if rooms previously owned by owner are empty; if so, delete those rooms
            for (Room room : this.user.getOwnedRooms()) {
                if (room.getConnectedUsers().size() == 0) {
                    this.poolServer.removeFromRooms(room);
                }
            }

            // close connection from server side
            this.user.getSocketConnection().close();

            // remove socket connection from pool handler opensockets
            this.poolServer.getPoolHandler().removeFromOpenSockets(this.user.getSocketConnection());

            // free default id if applicable
            if (this.user.getName().matches("^guest[1-9][0-9]*$")) {
                String guestIdNumber = this.user.getName().substring(5);
                int idNumber = Integer.parseInt(guestIdNumber);
                this.poolServer.removeFromDefaultIds(idNumber);
            }
        }
    }

    @Override
    public boolean checkValid() {
        return true;
    }

}
