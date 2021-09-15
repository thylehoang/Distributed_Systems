package Server.Commands;

public abstract class Command {
    public abstract void execute();
    public abstract boolean checkValid();
}
