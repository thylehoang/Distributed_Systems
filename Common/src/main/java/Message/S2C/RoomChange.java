package Message.S2C;

public class RoomChange {
    private String type = "roomchange";
    private String identity;
    private String former;
    private String roomid;

    public RoomChange(String identity, String former, String roomid) {
        this.identity = identity;
        this.former = former;
        this.roomid = roomid;
    }
}
