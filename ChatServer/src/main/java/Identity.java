import Server.Task;

public class Identity extends Task {
    public Identity(String former, String identity) {
        super();
    }

    @Override
    public void run() {
        //To do: create json object
        //type = indentitychange
        //indentity = indentity

        //check if the indentity not in used and follow the rule
        //rule: alphanumeric, >3 <16

        //if good
        //sent json object to all clients


        //else
        //sent json back to that client with identity = former

    }
}
