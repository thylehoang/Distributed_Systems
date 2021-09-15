package Message.S2C;

import java.util.ArrayList;

public class RoomContents {
    private String type = "roomcontents";
    private String roomid;
    private String owner;
    private ArrayList<String> identities;

    public RoomContents(String roomid, String owner, ArrayList<String> identities) {
        this.roomid = roomid;
        this.owner = owner;
        this.identities = identities;
    }
}
