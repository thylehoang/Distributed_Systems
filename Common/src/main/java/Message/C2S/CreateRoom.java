package Message.C2S;

public class CreateRoom {
    private String type = "createroom";
    private String roomid;

    public CreateRoom(String roomid) {
        this.roomid = roomid;
    }
}
