package Server.Commands;

import Message.IdentityChange;
import Server.ClientMeta;
import Server.PoolServer;
import com.google.gson.Gson;

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


            // notify the user of new identity
            IdentityChange identityChange = new IdentityChange(this.former, this.identity);
            poolServer.sendMessage(gson.toJson(identityChange), this.user);

            // notify all users of new identity

        }
        else {
            // invalid therefore send with former=identity
        }

    }

    @Override
    public boolean checkValid() {
        // check if provided name is valid (starts with upper/lower case, only consists of a-zA-Z0-9, >=3 and <=16 in length

        // check if already in use
        for (ClientMeta user : poolServer.getUsers()) {
            if (user.getName() == this.identity) {
                // already in use!
                return false;
            }
        }
        return true;
    }
}
