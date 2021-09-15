package Message.S2C;

import java.util.ArrayList;

public class RoomContents {
    private String type = "roomcontents";
    private String roomId;
    private String owner;
    private ArrayList<String> identities;

    public RoomContents(String roomId, String owner, ArrayList<String> identities) {
        this.roomId = roomId;
        this.owner = owner;
        this.identities = identities;
    }
}
