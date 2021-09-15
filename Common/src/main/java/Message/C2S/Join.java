package Message.C2S;

public class Join {
    private String type = "join";
    private String roomid;

    public Join(String roomid) {
        this.roomid = roomid;
    }
}
