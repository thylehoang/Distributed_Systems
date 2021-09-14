package Message;

public class RoomContents {
    private String type = "roomcontents";
    private String roomId;

    // TODO: figure out how to do identities list
    private String owner;

    public RoomContents(String roomId, String owner) {
        this.roomId = roomId;
        this.owner = owner;
    }
}
