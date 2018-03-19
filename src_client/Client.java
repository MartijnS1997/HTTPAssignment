import java.io.*;
import java.net.*;


/**
 * Created by Martijn on 7/03/2018.
 * A class of HTTP clients
 */
//Todo add functionality that if the sever is already connected it maintains the connection for requests
public class Client {

    public static final String ALREADY_CONNECTED = "Already connected, please close current connection and try again";

    //note: for our client we need:
    //a command line interpreter which runs in another thread and issues commands to out client
    public Client(){
        //nothing to construct so far
    }


    //Todo reconfigure such that it can be initialized with ClientCommand object
    public void initConnection(ClientCommand command){
        System.out.println("Issuing new connection");
        URL url = command.getUrl();
        int TCPPort = command.getPort();

        if(isAlreadyConnected()){
            throw new ClientException(ALREADY_CONNECTED);
        }

        //after connection set the current url
        this.setCurrentUrl(url);

        InetAddress ipAddress = getIPAddress(url);
        //now create the socket and the other connection parts
        try {
            Socket socket = new Socket(ipAddress, TCPPort);
            //PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            //now set them all
            setConnectionSocket(socket);
            //setPrintWriter(printWriter);
            setOutputStream(outputStream);
            setInputStream(inputStream);
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
            return false;
        }
        //if the socket is closed it means it is no longer needed and we can hook up an new connection
        return !connectionSocket.isClosed();
    }

    /**
     * Checks if the current active url has the same host as the requested url
     * @param otherUrl the other url to check
     * @return true if and only if both url's have the same host
     */
    public boolean hasSameHost(URL otherUrl){
        URL currentUrl = this.getCurrentUrl();
        //check if the url is already initialized
        if(currentUrl == null){
            //if so the host is never the same
            return false;
        }
        //if not check the hosts of the url's
        return (currentUrl.getHost()).equals(otherUrl.getHost());
    }

    /**
     * Closes the current connection by closing the streams and socket
     */
    public void closeConnection(){
        Socket socket = this.getConnectionSocket();
        PrintWriter writer = this.getPrintWriter();
        DataInputStream inputStream = this.getInputStream();

        //System.out.println("Closing down connection");
        try {
            socket.close();
            writer.close();
            inputStream.close();
        } catch (IOException e) {
            // the connection could not be closed for unknown reasons, later report back to the command client
            e.printStackTrace();
        }

    }

    /**
     * converts the given uri string into the corresponding IP address
     * @param url the url object of the requested page
     * @return and InetAddress object containing the IP address of the host
     */
    private InetAddress getIPAddress(URL url){
        InetAddress address = null;
        try {
            address = InetAddress.getByName(url.getHost());
        } catch (UnknownHostException e) {
            //if we are readying the command line send some feedback
            throw new ClientException("The the requested host is unknown, please try again\nhost: " + url.getHost());
        }
        return address;
    }

    /**
     * Issues the request and waits for a response
     * @param request the http request object containing the request
     */
    public String issueRequest(HttpRequest request) throws IOException {
        DataInputStream inputStream = this.getInputStream();
        //PrintWriter printWriter = this.getPrintWriter();
        DataOutputStream outputStream = this.getOutputStream();
        return request.execute(outputStream, inputStream);
    }

    /**
     * Getter for the output stream of the client, used for server communication
     * @return
     */
    private DataOutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Setter for the output stream used for server communication
     * @param outputStream the output stream to write to
     */
    private void setOutputStream(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Getter for the writer used for communication with the server (is able to print line for line)
     * @return the printer used in communication
     */
    @Deprecated
    private PrintWriter getPrintWriter() {
        return printWriter;
    }

    /**
     * Setter for the printer used for server communication
     * @param printWriter the print writer to be used by the client
     */
    @Deprecated
    private void setPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    /**
     * Getter for the data input stream used for server communication, receives the data for the client
     * @return an input stream
     */
    private DataInputStream getInputStream() {
        return inputStream;
    }

    /**
     * Setter for the input stream
     * @param inputStream the input stream to be used by the client
     */
    private void setInputStream(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * The socket that connects the client to the server
     * @return the socket that connects the client to the server
     */
    private Socket getConnectionSocket() {
        return connectionSocket;
    }

    /**
     * Setter for the connection socket
     * @param connectionSocket the socket responsible for the connection with the server
     */
    private void setConnectionSocket(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    /**
     * Getter for the currently used url, this url contains the path to the currently requested resource
     * as well as the host for the communication
     * @return the currently used url
     */
    private URL getCurrentUrl() {
        return currentUrl;
    }

    /**
     * Setter for the currently used url
     * @param currentUrl the url to be used in further communication with the server
     */
    private void setCurrentUrl(URL currentUrl) {
        this.currentUrl = currentUrl;
    }

    /**
     * The output stream to write to the servers
     */
    private DataOutputStream outputStream;

    /**
     * The print writer used in communication with the web page
     */
    private PrintWriter printWriter;

    /**
     * The buffered inputStream used to read from the input stream
     */
    private DataInputStream inputStream;

    /**
     * Socket to connect the client to the server
     */
    private Socket connectionSocket;

    /**
     * the url of the current connection
     */
    private URL currentUrl;
}



