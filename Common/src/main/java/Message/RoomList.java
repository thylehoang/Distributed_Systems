package Message;

import Message.Components.RoomListComponent;

import java.util.*;

public class RoomList {
    private String type = "roomlist";
    private ArrayList<RoomListComponent> roomList;

    public RoomList(HashMap<String,Integer> rooms) {
        this.roomList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : rooms.entrySet()) {
            this.roomList.add(new RoomListComponent(entry.getKey(), entry.getValue()));
        }
    }

}
