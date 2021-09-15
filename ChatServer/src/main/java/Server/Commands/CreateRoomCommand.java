package Server.Commands;

import Message.S2C.RoomList;
import Server.ClientMeta;
import Server.PoolServer;
import Server.Room;
import com.google.gson.Gson;

public class CreateRoomCommand extends Command {
    private PoolServer poolServer;
    private ClientMeta user;
    private String newRoomId;
    public static Gson gson = new Gson();

    public CreateRoomCommand(PoolServer poolServer, ClientMeta user, String newRoomId) {
        this.poolServer = poolServer;
        this.user = user;
        this.newRoomId = newRoomId;
    }

    @Override
    public void execute() {
        if (checkValid()) {
            // add room to pool server room list
            Room room = new Room(this.newRoomId, this.user);
            this.poolServer.addToRooms(room);

            // send RoomList to client
            RoomList roomList = new RoomList(this.poolServer.getRoomLists());
            this.poolServer.sendMessage(gson.toJson(roomList), this.user);
        }
    }

    @Override
    public boolean checkValid() {
        // check if provided name is valid (starts with upper/lower case, only consists of a-zA-Z0-9, >=3 and <=32 in length
        return this.newRoomId.matches("^[a-zA-Z][a-zA-Z0-9]{2,31}$");
    }
}
