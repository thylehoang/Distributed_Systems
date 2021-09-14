package Server.Commands;

import Message.IdentityChange;
import Server.ClientMeta;
import Server.PoolServer;
import Server.Room;
import com.google.gson.Gson;

import java.util.HashSet;

public class IDChangeCommand extends Command{
    private PoolServer poolServer;
    private ClientMeta user;
    private String former;
    private String identity;
    public static Gson gson = new Gson();

    public IDChangeCommand(PoolServer poolServer, ClientMeta user, String newIdentity) {
        this.user = user;
        this.poolServer = poolServer;
        this.former = user.getName();
        this.identity = newIdentity;
    }

    @Override
    public void execute() {
        if (checkValid()) {
            // update stored name in pool server (clientmeta, owned rooms, and currently connected room's client list)
            this.user.setName(this.identity);

//            for (Room room : this.user.getOwnedRooms()) {
//                // TODO: not sure if this updates automatically or if i need to do it manually like this
//                room.setOwner(this.user);
//            }

            // notify all users (including this user) of new identity
            IdentityChange identityChange = new IdentityChange(this.former, this.identity);
            poolServer.broadcastMessageToAll(gson.toJson(identityChange));
        }
        else {
            // invalid therefore send with former=identity
            IdentityChange identityChange = new IdentityChange(this.former, this.former);
            poolServer.sendMessage(gson.toJson(identityChange), this.user);
        }

    }

    @Override
    public boolean checkValid() {
        // check if provided name is valid (starts with upper/lower case, only consists of a-zA-Z0-9, >=3 and <=16 in length
        if (!this.identity.matches("^[a-zA-Z][a-zA-Z0-9]{2,15}$")) {
            return false;
        }

        // check if already in use
        for (ClientMeta user : poolServer.getUsers()) {
            if (user.getName().equals(this.identity)) {
                // already in use!
                return false;
            }
        }
        return true;
    }
}
