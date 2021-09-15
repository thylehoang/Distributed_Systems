package Message.C2S;

public class IdentityChange {
    private String type = "identitychange";
    private String identity;

    public IdentityChange(String identity) {
        this.identity = identity;
    }
}
