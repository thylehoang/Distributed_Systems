package Message.S2C;

import Message.Components.RoomListComponent;

import java.util.*;

public class RoomList {
    private String type = "roomlist";
    private ArrayList<RoomListComponent> rooms;

    public RoomList(HashMap<String,Integer> roomsMap) {
        this.rooms = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : roomsMap.entrySet()) {
            this.rooms.add(new RoomListComponent(entry.getKey(), entry.getValue()));
        }
    }

}
