import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//Todo add support for timestamps in the virtual file system
/**
 * Created by Martijn on 10/03/2018.
 * A class of http server objects
 */
public class Server {

    public Server(){

    }

    /**
     * Main loop of the server itself
     */
    public void serverLoop(){
        initServer();
        listenForConnections();
    }

    /**
     * Initializes the server by creating a new thread pool and a server socket that listens for connections
     */
    private void initServer(){
        //create a cached thread pool, a cached thread pool keeps released threads for 60sec
        //if the threads are not reclaimed after that the resources are released
        this.threadPool = Executors.newCachedThreadPool();
        try {
            this.serverSocket = new ServerSocket(getTcpPort());
        } catch (IOException e) {
            //couldn't initialize, let it fly
            e.printStackTrace();
        }
        //initialize the file server system
        initFileServer();
    }

    /**
     * Listens for connections and accepts them creating new threads for each connection
     */
    private void listenForConnections(){
        //get the thread pool
        ExecutorService threadPool = this.getThreadPool();
        //get the server socket
        ServerSocket serverSocket = this.getServerSocket();
        //initialize control variable:
        boolean errorOccurred = false;
        while(!errorOccurred){

            try {
                //listen for connections
                Socket connectionSocket = serverSocket.accept();
                //if a new connection is requested create a new server connection object
                ServerConnection connection = new ServerConnection(connectionSocket, this);

                System.out.println("created connection: " + connection.toString());
                //submit the new connection to the thread pool
                threadPool.submit(connection);
            } catch (IOException e) {
                errorOccurred = true;
                e.printStackTrace();
            }
        }
        //we exited the loop, shut down the server
        this.terminate();
    }

    /**
     * Terminates the server by waiting for all the connections to finish and then closing down the socket
     */
    private void terminate(){
        ServerSocket serverSocket = this.getServerSocket();
        ExecutorService threadPool = this.getThreadPool();
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            serverSocket.close();
        } catch (IOException | InterruptedException e) {
            //idk what happened just go with it, we were closing down the server already
            e.printStackTrace();
        }
    }

    /**
     * Initializes the file server by transferring all the pages into the virtual file system
     */
    private void initFileServer(){
        ServerFileSystemInitializer.initServerFiles(this.getFileSystem());
    }


    /**
     * Getter for the server socket, used to listen for connections
     * @return the server socket used for listening for external connections
     */
    private ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Getter for the executor service that maintains the thread pool for the connections
     * @return the thread pool used to maintaining the connections
     */
    private ExecutorService getThreadPool() {
        return threadPool;
    }

    /**
     * Getter for the server file system, the server filesystem is a virtual file system
     * that is only in memory when in execution (contents are lost when we shut down the server)
     * @return the serer file system used by the server
     */
    public ServerFileSystem getFileSystem() {
        return fileSystem;
    }

    /**
     * The socket that maintains the connection to the server
     */
    private ServerSocket serverSocket;

    /**
     * The thread pool used for connection creation
     */
    private ExecutorService threadPool;

    /**
     * The fileSystem used by the server
     */
    private ServerFileSystem fileSystem = new ServerFileSystem();

    /**
     * Getter for the TCP port used for communication with the server
     * @return the TCP port used for communication
     */
    public static int getTcpPort() {
        return TCP_PORT;
    }

    /**
     * The tcp port used by the server to maintain the connections, we're building a http server so 80 it is
     */
    private final static int TCP_PORT = 80;
}
