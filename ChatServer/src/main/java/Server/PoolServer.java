package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PoolServer{
    private boolean alive = false;
    public static final int PORT = 4444;
//    private ThreadPool threadPool;
    private PoolHandler poolHandler;
//    private int nThreads;
//    private final List<PoolServer.ThreadWorker> threads;
//    private final BlockingQueue<Runnable> taskQueue;
//    private List<PoolServer.ChatConnection> connectionList = new ArrayList<>();

//    //to store the rooms
//    private HashMap<String, List<String>> rooms;
//
//    //to store owner of certain rooms
//    private HashMap<String, String> owners;
//
//
//    //this to store all the interger are occupied from users
//    private List<Integer> id;

    public PoolServer() {
//        this.nThreads = nThreads;
//        this.threadPool = new ThreadPool(nThreads);
        this.poolHandler = new PoolHandler(2);
    }

    //main functions
    public static void main(String[] args) {
        PoolServer poolServer = new PoolServer();
        // run the pool handler on separate thread
        Thread t = new Thread(poolServer.poolHandler);
        t.start();

        poolServer.handle();
    }

    //this is to create a pool of thread within the server
//    public PoolServer() {
////        this.threads = new ArrayList<>();
////        this.taskQueue = new LinkedBlockingQueue<>();
////        for (int i = 0; i < nThreads; i++) {
////            threads.add(new PoolServer.ThreadWorker());
////            threads.get(i).start();
////        }
//    }

    //add new connection into list //should we make this public???
//    public void join(PoolServer.ChatConnection conn) {
//        synchronized (connectionList) {
//            connectionList.add(conn);
//        }
//        //synchronized (id){
//            //Collections.sort(id);
//            //for(int i = 0; i< id.size(); i++){
//                //if i not in id
//                ///new identity = guest + i
//                //break
//            //}
//        //}
//    }

    //if the connection receive an task, add to the list then notify the worker
//    public void execute(Runnable task) {
//        synchronized (taskQueue) {
//            taskQueue.add(task);
//            taskQueue.notify();
//        }
//    }

    private void handle() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.printf("listening on port %d\n", PORT);
            alive = true;
            while (alive) {
                // wait until a new client connects (.accept is a blocking method)
                Socket socket = serverSocket.accept();
                System.out.printf("New connection established! Adding to pool handler open sockets\n");
                if (socket != null) {
                    SocketConnection socketConnection = new SocketConnection(socket);
                    poolHandler.addToOpenSockets(socketConnection);
                }
//                openSockets.add(socket);

//                ConnectionTask connTask = new ConnectionTask(socket);
//                threadPool.execute(connTask);
//                ChatConnection connection = new ChatConnection(socket);
//                join(connection);
//                connection.run(); //?????
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //each threads in the pools
//    private class ThreadWorker extends Thread {
//        //wait for the task to add in the queue then execute it
//        @Override
//        public void run() {
//            Runnable task;
//            while (true){
//                synchronized (taskQueue){
//                    while (taskQueue.isEmpty()){
//                        try {
//                            taskQueue.wait();
//                        } catch (InterruptedException e) {
//                            System.out.printf("Receive interruption, %s\n", e.getMessage());
//                        }
//                    }
//                    task = taskQueue.poll();
//                }
//                task.run();
//            }
//        }
//    }

    //chat connection - each connection is a seperate threads to hold the connection
//    private class ChatConnection extends Thread{
//        private Socket socket;
//        private BufferedReader reader;
//        private PrintWriter writer;
//        private boolean connectionAlive = false;
//        private Task task = null;
//        private String identity;
//
//
//        public ChatConnection(Socket socket) throws IOException {
//            this.socket = socket;
//            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            this.writer = new PrintWriter(socket.getOutputStream());
//        }
//
//        @Override
//        public void run() {
//            connectionAlive = true;
//            while (connectionAlive) {
//                try {
//                    String in  = reader.readLine();
//                    if (in != null) {
//                        //check whether it is a task
//                        //check it type
//
//                        //get json object
//                        //if (in.length() ==0){
//                        // .....
//                        //}else{
//                        // Map<String, Integer> map = new Gson().fromJson(int, new TypeToken<HashMap<String, String>>() {}.getType());
//                        //}
//                        //Server.Task task = new Server.Task(put Hashmap in here for create????);
//                        //execute(task);
//                    } else {
//                        connectionAlive = false;
//                    }
//                } catch (IOException e) {
//                    connectionAlive = false;
//                }
//            }
//            close();
//        }
//
//        public void close() {
//            try {
//                socket.close();
//                reader.close();
//                writer.close();
//            } catch (IOException e) {
//                System.out.println(e.getMessage());
//            }
//        }
//    }

    //this is creation of one task
//    private class Task implements Runnable{
//        public Task createTask(Object jsonObj){
//            /////
//            return null;
//        }
//
//    }


}
