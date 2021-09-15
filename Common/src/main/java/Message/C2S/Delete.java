package Message.C2S;

public class Delete {
    private String type = "delete";
    private String roomid;

    public Delete(String roomid){
        this.roomid = roomid;
    }
}
