package Message;

public class IdentityChange {
    private String type = "newidentity";
    private String former;
    private String identity;

    public IdentityChange(String former, String identity) {
        this.former = former;
        this.identity = identity;
    }

    public void execute() {
    }
}
