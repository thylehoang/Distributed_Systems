package Message;

public class Join {
    private String type = "join";
    private String roomId;

    public Join(String roomId) {
        this.roomId = roomId;
    }
}
