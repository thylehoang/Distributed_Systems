package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PoolHandler implements Runnable {
    /*
     Handles thread scheduling with the pool
     */

    public static LinkedBlockingQueue<SocketConnection> openSockets;
    private ThreadPool threadPool;

    public PoolHandler(int nThreads) {
        this.threadPool = new ThreadPool(nThreads);
        openSockets = new LinkedBlockingQueue<SocketConnection>();
    }

    @Override
    public void run() {
        // constantly pull socket from socket queue, delegate to threadpool to process, then put back into socket queue
        // (unless socket closes)
        while (true) {
            try {
                SocketConnection socketConnection = openSockets.poll(50, TimeUnit.MICROSECONDS);
                if (socketConnection != null) {
//                    System.out.printf("Looking at socket [%d]\n", socketConnection.getSocket().getPort());
                    if (socketConnection.getSocket().isClosed()) {
                        System.out.printf("Socket [%d] is closed! Not re-adding to queue, and continuing\n",
                                socketConnection.getSocket().getPort());
                        continue;
                    }
//
                    try {
                        if (socketConnection.getReader().ready()) {
                            System.out.printf("Reading from [%d] buffer\n", socketConnection.getSocket().getPort());
                            String in = socketConnection.getReader().readLine();
                            if (in != null) {
                                LineProcessor lineProcessor = new LineProcessor(in, socketConnection.getSocket().getPort());
                                threadPool.execute(lineProcessor);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }
                    openSockets.put(socketConnection);
                }
//                else {
//                    System.out.printf("No sockets in queue!\n");
//                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addToOpenSockets(SocketConnection socketConnection) {
        try {
            openSockets.put(socketConnection);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
