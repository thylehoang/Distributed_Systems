import Server.Task;

public class RContent extends Task {
    private String identity;
    private String roomID;
    public RContent(String identity, String roomID) {
        super();
        this.identity = identity;
        this.roomID = roomID;
    }

    @Override
    public void run() {
        //To Do: create json object with
        //type = roomcontents
        //roomid
        //identities = [list of members]
        //owner = owner

        //Owner of MainHall is empty
        //To client only
    }
}
