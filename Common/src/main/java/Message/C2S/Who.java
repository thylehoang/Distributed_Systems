package Message.C2S;

public class Who {
    private String type = "who";
    private String roomId;

    public Who(String roomId) {
        this.roomId = roomId;
    }
}
