import Server.Task;

public class RChange extends Task {
    private String former;
    private String identity;
    private String roomId;
    public RChange(String former, String identity, String roomID) {
        super();
        this.former = former;
        this.identity = identity;
        this.roomId = roomID;
    }

    @Override
    public void run() {
        //To do: create a json in that room
        // type = roomchange
        // identity= identity
        // former = former
        // roomid = roomid

        //if success change room:
        //add the client into the room
        //sent json to all the client in the room

        //else
        //sent json to only that client
    }
}
