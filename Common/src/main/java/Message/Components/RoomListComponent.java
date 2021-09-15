package Message.Components;

public class RoomListComponent {
    private String roomid;
    private int count;

    public RoomListComponent(String roomid, int count) {
        this.roomid = roomid;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public String getRoomid() {
        return roomid;
    }
}
