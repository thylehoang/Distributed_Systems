package Server;

public class LineProcessor implements Runnable {
    private String line;
    private int port;

    public LineProcessor(String line, int port) {
        this.line = line;
        this.port = port;
    }

    @Override
    public void run() {
        System.out.printf("[%s] %d: %s\n", Thread.currentThread().getName(), port, line);
    }
}
