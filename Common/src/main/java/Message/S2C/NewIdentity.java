package Message.S2C;

public class NewIdentity {
    private String type = "newidentity";
    private String former;
    private String identity;

    public NewIdentity(String former, String identity) {
        this.former = former;
        this.identity = identity;
    }

    public void execute() {
    }
}
