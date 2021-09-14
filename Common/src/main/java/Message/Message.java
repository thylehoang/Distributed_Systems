package Message;

public class Message {
    private String type = "message";
    private String identity;
    private String content;

    public Message(String identity, String content) {
        this.identity = identity;
        this.content = content;
    }
}
