package Server;
import Connection.SocketConnection;
import Server.Commands.QuitCommand;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PoolHandler implements Runnable {
    /*
     Handles thread scheduling with the pool
     */
    private PoolServer poolServer;
    public static LinkedBlockingQueue<SocketConnection> openSockets;
    private ThreadPool threadPool;

    public PoolHandler(int nThreads, PoolServer poolServer) {
        this.threadPool = new ThreadPool(nThreads);
        this.poolServer = poolServer;
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
                    try {
                        if (socketConnection.getReader().ready()) {
                            System.out.printf("Reading from [%d] buffer\n", socketConnection.getSocket().getPort());
                            String in = socketConnection.getReader().readLine();
                            if (in != null) {
                                LineProcessor lineProcessor = new LineProcessor(socketConnection, in, poolServer);
                                threadPool.execute(lineProcessor);
                            }
                        }

                    } catch (IOException e) {
                        // client disconnected -- treat as quit message
                        System.out.printf("Client [%d] disconnected! (Failed buffer ready check, or cannot write to socket)\n",
                                socketConnection.getSocket().getPort());
                        // get user from pool server
                        disconnectSocket(socketConnection);
//                        e.printStackTrace();
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

    public ClientMeta getSocketUser(SocketConnection socketConnection) {
        for (ClientMeta user : this.poolServer.getUsers()) {
            if (user.getSocketConnection().equals(socketConnection)) {
                return user;
            }
        }
        return null;
    }

    public void removeFromOpenSockets(SocketConnection socketConnection) {
        openSockets.remove(socketConnection);
    }

    private void disconnectSocket(SocketConnection socketConnection) {
        ClientMeta user = getSocketUser(socketConnection);
        // to prevent double calling, we also check if the user has already been set to have 'disconnected' status
        if (user != null && !user.getDisconnected()) {
            QuitCommand quitCommand = new QuitCommand(this.poolServer, user);
            quitCommand.execute();
        }
    }
}
