package Client;

import Connection.SocketConnection;
import Message.Components.RoomListComponent;
import Message.S2C.RoomContents;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ClientLineProcessor {
    private ChatClient chatClient;
    private SocketConnection socketConnection;
    public static Gson gson = new Gson();

    public ClientLineProcessor(SocketConnection socketConnection, ChatClient chatClient) {
        this.chatClient = chatClient;
        this.socketConnection = socketConnection;
    }

    public void processLine(String jsonLine) {
        JsonElement jsonElement = JsonParser.parseString(jsonLine);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement typeElement = jsonObject.get("type");
        String type = typeElement.getAsString();

        String output = null;

        if (type.equals("message")) {
            output = handleMessage(jsonObject);
        }
        else if (type.equals("newidentity")) {
            output = handleNewIdentity(jsonObject);
        }
        else if (type.equals("roomchange")) {
            output = handleRoomChange(jsonObject);
        }
        else if (type.equals("roomcontents")) {
            output = handleRoomContents(jsonObject);
        }
        else if (type.equals("roomlist")) {
            output = handleRoomList(jsonObject);
        }

        // print to stdout
        this.chatClient.getWriter().print(output);
        this.chatClient.getWriter().flush();
    }

    public String handleMessage(JsonObject jsonObject) {
        String senderId = jsonObject.get("identity").getAsString();
        String content = jsonObject.get("content").getAsString();
        return String.format("[%s] %s: %s\n", this.chatClient.getRoomId(), senderId, content);
    }

    public String handleNewIdentity(JsonObject jsonObject) {
        String former = jsonObject.get("former").getAsString();
        String identity = jsonObject.get("identity").getAsString();

        if (former.equals(this.chatClient.getRoomId())) {
            // pertaining to this client
            if (former.equals(identity)) {
                // did not change
                return "Requested identity is invalid or in use\n";
            }
            else {
                // update chat client's idea of the current name
                this.chatClient.setName(identity);
            }
        }
        return String.format("%s is now %s\n", former, identity);
    }

    public String handleRoomChange(JsonObject jsonObject) {
        String identity = jsonObject.get("identity").getAsString();
        String former = jsonObject.get("former").getAsString();
        String roomId = jsonObject.get("roomid").getAsString();

        if (identity.equals(this.chatClient.getName())) {
            // pertaining to this client
            if (former.equals(roomId)) {
                // did not change (should only be sent to the person trying to change and it failed)
                return "The requested room is invalid or non existent";
            }
            else {
                // update chat client's idea of the current room
                this.chatClient.setRoomId(roomId);
            }

            // check if disconnecting
            if (roomId.equals("")) {
                // disconnect!
                this.socketConnection.close();
                this.chatClient.setKeepAlive(false);
            }
        }

        if (former.equals("")) {
            return String.format("%s moved to %s\n", identity, roomId);
        }

        return String.format("%s moved from %s to %s\n", identity, former, roomId);
    }

    public String handleRoomContents(JsonObject jsonObject) {
        String roomId = jsonObject.get("roomid").getAsString();
        String owner = jsonObject.get("owner").getAsString();

        // get identities list
        JsonArray identitiesArray = jsonObject.getAsJsonArray("identities");
        String[] identities = gson.fromJson(identitiesArray, String[].class);
        List<String> identitiesList = Arrays.asList(identities);

        String identitiesStr = "";

        for (String identity : identitiesList) {
            if (identity.equals(owner)) {
                identitiesStr = identitiesStr + identity + "* ";
            }
            else {
                identitiesStr = identitiesStr + identity + " ";
            }
        }

        return String.format("%s contains %s\n", roomId, identitiesStr);
    }

    public String handleRoomList(JsonObject jsonObject) {
        // get room list component list
        JsonArray roomListArray = jsonObject.getAsJsonArray("rooms");
        RoomListComponent[] roomListComponents = gson.fromJson(roomListArray, RoomListComponent[].class);
        List<RoomListComponent> roomListComponentList = Arrays.asList(roomListComponents);

        String output = "";

        for (RoomListComponent roomListComponent : roomListComponentList) {
            output = output + String.format("%s: %d guests\n", roomListComponent.getRoomid(), roomListComponent.getCount());
        }

        return output;
    }
}
