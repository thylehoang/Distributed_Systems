package Message.S2C;

public class RoomChange {
    private String type = "roomchange";
    private String identity;
    private String former;
    private String roomId;

    public RoomChange(String identity, String former, String roomId) {
        this.identity = identity;
        this.former = former;
        this.roomId = roomId;
    }
}
