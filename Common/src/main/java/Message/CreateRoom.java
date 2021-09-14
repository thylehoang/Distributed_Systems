package Message;

public class CreateRoom {
    private String type = "createroom";
    private String roomId;

    public CreateRoom(String roomId) {
        this.roomId = roomId;
    }
}
