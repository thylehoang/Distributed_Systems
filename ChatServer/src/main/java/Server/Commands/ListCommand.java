package Server.Commands;

import Message.RoomList;
import Server.ClientMeta;
import Server.PoolServer;
import com.google.gson.Gson;

public class ListCommand extends Command {
    private PoolServer poolServer;
    private ClientMeta user;
    public static Gson gson = new Gson();

    public ListCommand(PoolServer poolServer, ClientMeta user) {
        this.poolServer = poolServer;
        this.user = user;
    }

    @Override
    public void execute() {
        if (checkValid()) {
            RoomList roomList = new RoomList(this.poolServer.getRoomLists());
            this.poolServer.sendMessage(gson.toJson(roomList), this.user);
        }
    }

    @Override
    public boolean checkValid() {
        return true;
    }

}
