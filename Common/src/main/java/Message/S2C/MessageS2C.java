package Message.S2C;

public class MessageS2C {
    private String type = "message";
    private String identity;
    private String content;

    public MessageS2C(String identity, String content) {
        this.identity = identity;
        this.content = content;
    }
}
