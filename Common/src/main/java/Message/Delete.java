package Message;

public class Delete {
    private String type = "delete";
    private String roomId;

    public Delete(String roomId){
        this.roomId = roomId;
    }
}
