import java.io.*;
import java.net.*;
import java.nio.Buffer;
import java.util.Scanner;


/**
 * Created by Martijn on 7/03/2018.
 * A class of HTTP clients
 */
public class Client {

    public static final String ALREADY_CONNECTED = "Already connected, please close current connection and try again";

    //note: for our client we need:
    //a command line interpreter which runs in another thread and issues commands to out client
    public Client(){
        //nothing to construct so far
    }


    //Todo reconfigure such that it can be initialized with ClientCommand object
    private void initConnection(String uriString, int TCPPort){

        if(isAlreadyConnected()){
            throw new IllegalStateException(ALREADY_CONNECTED);
        }

        InetAddress ipAddress = getIPAddress(uriString);
        //now create the socket and the other connection parts
        try {
            Socket socket = new Socket(ipAddress, TCPPort);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader readBuffer = new BufferedReader( inputStreamReader);

            //now set them all
            setConnectionSocket(socket);
            setPrintWriter(printWriter);
            setReader(readBuffer);
        } catch (IOException e) {
            //something went wrong, notify the command line
            e.printStackTrace();
        }

        //the connection is now successfully initialized

    }

    /**
     * Checks if the client is already connected
     * @return returns true if the client is already connected
     */
    private boolean isAlreadyConnected(){
        Socket connectionSocket = this.getConnectionSocket();
        if(connectionSocket == null){
            return true;
        }
        //if the socket is closed it means it is no longer needed and we can hook up an new connection
        return connectionSocket.isClosed();
    }

    /**
     * Closes the current connection by closing the streams and socket
     */
    private void closeConnection(){
        Socket socket = this.getConnectionSocket();
        PrintWriter writer = this.getPrintWriter();
        BufferedReader reader = this.getReader();

        try {
            socket.close();
            writer.close();
            reader.close();
        } catch (IOException e) {
            // the connection could not be closed for unknown reasons, later report back to the command client
            e.printStackTrace();
        }

    }

    /**
     * converts the given uri string into the corresponding IP address
     * @param uriString the string containing the url of the requested page
     * @return and InetAddress object containing the IP address of the host
     */
    private InetAddress getIPAddress(String uriString){
        InetAddress address = null;
        try {
            URL url = new URL(uriString);
            address = InetAddress.getByName(url.getHost());
        } catch (UnknownHostException | MalformedURLException e) {
            //if we are readying the command line send some feedback
            e.printStackTrace();
        }
        return address;
    }

    /**
     * Issues the request and waits for a response
     * @param request the http request object containing the request
     */
    public void issueRequest(HttpRequest request){
        BufferedReader reader = this.getReader();
        PrintWriter printWriter = this.getPrintWriter();
        request.execute(printWriter, reader);
    }


    private PrintWriter getPrintWriter() {
        return printWriter;
    }

    private void setPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    private BufferedReader getReader() {
        return reader;
    }

    private void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public Socket getConnectionSocket() {
        return connectionSocket;
    }

    public void setConnectionSocket(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    private PrintWriter printWriter;
    private BufferedReader reader;
    private Socket connectionSocket;

}

//    public static void main(String args[]){
//        Client client = new Client("http://example.com", 80);
//        client.initConnection();
//        try {
//            client.executeCommand();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    //Todo expand when http nits and grits are known
//    public Client(String url, int tcpPort) {
//        setAddress(url);
//        setPort(tcpPort);
//    }
//
//    public void initConnection(){
//        InetAddress ipAddress = this.getAddress();
//        int tcpPort = this.getPort();
//        Socket socket = null;
//        //create the connection:
//        try {
//            socket = new Socket(ipAddress, tcpPort);
//            //once we have sockets, init the data streams
//            //InputStream inputStream = socket.getInputStream();
//            //we use a buffer for now, we can read the incoming lines from the other side
//            //DataInputStream dataInputStream = new DataInputStream(inputStream);
//            //we use an output stream at the moment, may change later on
//            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
//            //setInputStream(dataInputStream);
//            setOutputStream(outputStream);
//            setSocket(socket);
//        } catch (IOException e) {
//            //I have no idea what happened
//            e.printStackTrace();
//        }
//
//        System.out.println("connection established");
//
//    }
//
//
//    private void getCommand(){
//
//    }
//
//    public void executeCommand() throws IOException {
//        System.out.println("executing command");
//        String command = "GET http://example.com/index.html HTTP/1.1";
//        DataOutputStream output = this.getOutputStream();
//        //DataInputStream input = this.getInputStream();
//        byte bytebuffer[] = new  byte[1];
//        String content= null;
//        try {
//            InputStream inputStream = this.getSocket().getInputStream();
//            DataInputStream input = new DataInputStream(inputStream);
//            output.writeBytes(command);
//            System.out.println("command issued");
//            int len = input.read(bytebuffer);
//
//            System.out.println("waiting for response");
//            //System.out.println("Response length: " + inputData);
//            System.out.println("response length: " + len);
//        } catch (IOException e) {
//            //nothing
//        }
//
//
//    }
//
//
//    private int getPort() {
//        return port;
//    }
//
//    private void setPort(int port) {
//        this.port = port;
//    }
//
//    private Socket getSocket() {
//        return socket;
//    }
//
//    private void setSocket(Socket socket) {
//        this.socket = socket;
//    }
//
//    private DataOutputStream getOutputStream() {
//        return outputStream;
//    }
//
//    private void setOutputStream(DataOutputStream outputStream) {
//        this.outputStream = outputStream;
//    }
//
//    private DataInputStream getInputStream() {
//        return inputStream;
//    }
//
//    private void setInputStream(DataInputStream inputStream) {
//        this.inputStream = inputStream;
//    }
//
//    private InetAddress getAddress() {
//        return address;
//    }
//
//    private void setAddress(String urlString) {
//        InetAddress address = null;
//        try {
//            address = InetAddress.getByName((new URL(urlString)).getHost());
//        } catch (UnknownHostException | MalformedURLException e) {
//            //just leave it open for now, change if we get issues
//            e.printStackTrace();
//        }
//        this.address = address;
//    }
//
//    /**
//     * Variable that stores the ip adress of the website we want to visit
//     */
//    InetAddress address;
//
//    /**
//     * The variable that stores the TCP port used for communication with the server
//     */
//    private int port = 80;
//
//    /**
//     * The socket used in the connection with the server
//     */
//    private Socket socket;
//
//    /**
//     * Object that holds the stream that sends data to the server
//     */
//    private DataOutputStream outputStream;
//
//    /**
//     * Object that holds the stream that receives data from the server
//     */
//    private DataInputStream inputStream;


