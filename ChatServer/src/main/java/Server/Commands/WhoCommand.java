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
    public static Gson gson = new Gson();

    public WhoCommand(PoolServer poolServer, ClientMeta user) {
        this.user = user;
        this.poolServer = poolServer;
    }

    @Override
    public void execute() {
        if (checkValid()) {
            // get room user is currently connected to
            String roomId = user.getConnectedRoom().getRoomId();
            String owner = user.getConnectedRoom().getOwner().getName();

            RoomContents roomContents = new RoomContents(roomId, owner, user.getConnectedRoom().getConnectedUserNames());
            poolServer.sendMessage(gson.toJson(roomContents), user);
        }
    }

    @Override
    public boolean checkValid() {
        return true;
    }

}
