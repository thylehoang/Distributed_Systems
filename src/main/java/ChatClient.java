import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatClient {
    private static final int PORT = 6379;
    private boolean alive = false;
    private Socket clientSocket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private BufferedReader stdreader;

    private String want;

    public void setWant(String want) {
        this.want = want;
    }

    public String getWant() {
        return want;
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.connect();
    }

    private void connect() {
        //Socket clientSocket;
        try {
            clientSocket = new Socket(InetAddress.getLocalHost(),PORT);
            this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.stdreader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Connected to the chat server");
            alive = true;
            while(alive){
                Connection connection = new ChatClient.Connection(clientSocket, writer, reader);
                Handler handler = new Handler(clientSocket, writer, stdreader);
                connection.start();
                handler.start();
            }
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        clientSocket.close();
        writer.close();
        reader.close();
        stdreader.close();
    }

    public void sendMessage(String message) {
        try {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            alive = false;
            e.printStackTrace();
        }

    }

    //handle connection with server
    private class Connection extends Thread {
        private Socket socket;
        private BufferedReader reader;
        private boolean connectionAlive = false;
        private Gson gson;
        private BufferedWriter writer;


        private List<String> owner_rooms;
        private String identity;
        private String currentroom;

        public Connection(Socket socket, BufferedWriter writer, BufferedReader reader) throws IOException {
            this.socket = socket;
            this.gson = new Gson();
            this.reader = reader;
            this.writer = writer;
        }

        private void handleReply(Map<String, Object> in){
            Object command = in.get("type");
            Object identity, former, roomid, content,identities, owner;
            String want;
            List<String> l;
            if (command.equals("newidentity")){
                former = in.get("former");
                identity = in.get("identity");
                if (former==null){
                    System.out.printf("%s is now %s\n", identity, identity);
                }else if (identity.equals(former)){
                    System.out.println("Reguested identity invalid or in use");
                }else{
                    System.out.printf("%s is now %s\n", former, identity);
                }
            }else if (command.equals("roomchange")){
                identity = in.get("identity");
                former = in.get("former");
                roomid = in.get("roomid");
                if (former==null){
                    System.out.printf("%s move to %s\n", roomid);
                }else if (identity.equals(former)){
                    System.out.println("The reguested room is invalid or non existent");
                }else{
                    System.out.printf("%s move from %s to %s\n",identity, former, roomid);
                }
            }else if (command.equals("roomlist")){
                want = getWant();
                //how to convert the room???
            }else if (command.equals("message")){
                identity = in.get("identity");
                content = in.get("content");
                System.out.printf("%s: %s\n",identity, content);
            }else if (command.equals("roomcontents")){
                identities = in.get("identities");
                roomid = in.get("roomid");
                owner = in.get("owner");
                if (identities instanceof List){
                    l  = (List<String>) identities;
                    if(owner.equals("")){
                        System.out.printf("%s contains %s\n",roomid, String.join(" ", l));
                    }else{
                        System.out.printf("%s contains %s %s*\n",roomid, String.join(" ", l), owner);
                    }
                }
            }
        }

        private Map<String, Integer> printContents(Object list){
            if (list instanceof List){
                list = (List<Object>) list;
                //.....
            }
            return null;
        }

        @Override
        public void run() {
            connectionAlive = true;
            Map<String, String> rsp;
            while (connectionAlive) {
                try {
                    String in = reader.readLine();
                    if (in != null) {
                        rsp = gson.fromJson(in, Map.class);
                    } else {
                        connectionAlive = false;
                    }
                } catch (IOException e) {
                    connectionAlive = false;
                }
            }
            try {
                ChatClient.this.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //to handle standard input
    private class Handler extends Thread {
        private BufferedReader reader;
        private Gson gson;
        private BufferedWriter writer;
        private boolean connectionAlive=false;
        private Socket socket;

        public Handler(Socket socket, BufferedWriter writer, BufferedReader reader) throws IOException {
            this.gson = new Gson();
            this.reader = reader;
            this.writer = writer;
        }

        private JsonObject handleRequest(String[] inps){
            String command = inps[0];
            String rsp;
            if (command.equals("newidentity")){
                rsp = String.format("{\"type\": %s,\n \"identity\": %s\n}", command, inps[1]);
            }else if (command.equals("join")){
                rsp = String.format("{\"type\": %s,\n \"roomid\": %s\n}", command, inps[1]);
            }else if (command.equals("who")){
                rsp = String.format("{\"type\": %s,\n \"roomid\": %s\n}", command, inps[1]);
            }else if (command.equals("list")){
                rsp = String.format("{\"type\": %s\n}", command);
            }else if (command.equals("createroom")){
                setWant(inps[1]);
                rsp = String.format("{\"type\": %s,\n \"roomid\": %s\n}", command, inps[1]);
            }else if (command.equals("delete")){
                rsp = String.format("{\"type\": %s,\n \"roomid\": %s\n}", command, inps[1]);
            }else if (command.equals("quit")){
                rsp = String.format("{\"type\": %s\n}", command);
            }else{
                rsp = String.format("{\"type\": message,\n \"content\": %s\n}", command);
            }
            return gson.fromJson(rsp, JsonObject.class);
        }

        private void handleInput(String inp){
            Pattern pattern = Pattern.compile("#[a-zA-Z]\\s[a-zA-Z]+");
            Matcher match = pattern.matcher(inp);
            boolean command = match.matches();
            if (command) {
                inp = inp.substring(1);
            }
            String[] spls = inp.split("\\s+");
            JsonObject rsp = handleRequest(spls);
            sendMessage(rsp.toString());
        }

        @Override
        public void run() {
            connectionAlive = true;
            while (connectionAlive) {
                try {
                    String in = reader.readLine();
                    if (in != null) {
                        handleInput(in);
                    } else {
                        connectionAlive = false;
                    }
                } catch (IOException e) {
                    connectionAlive = false;
                }
            }
            try {
                ChatClient.this.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
