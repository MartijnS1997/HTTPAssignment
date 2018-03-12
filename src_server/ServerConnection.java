import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martijn on 10/03/2018.
 * A class of server connections, each connection serves one client at a time
 */
//Todo implement the run sequence and the subsequent reading procedures
public class ServerConnection implements Runnable {

    /**
     * Constructor for a server connection object
     * @param socket the connection socket to communicate with the client
     */
    public ServerConnection(Socket socket, Server server){
        this.connectionSocket = socket;
    }

    @Override
    public void run() {
        //the sequence for the main loop
        //1. read the request
        //2. parse it
        //3. respond using the newly created request
        //4. goto 1.

        initConnection();
        readRequest();



    }

    /**
     * Initiates the connection by creating the streams necessary for communication with the clients
     */
    private void initConnection(){
        Socket socket = this.getConnectionSocket();

        try {
            InputStream inputStream = socket.getInputStream();
            this.reader = new BufferedReader(new InputStreamReader(inputStream));

            OutputStream outputStream = socket.getOutputStream();
            this.printWriter = new PrintWriter(outputStream);
        }catch(IOException e){
            //something went wrong, dunno what
            e.printStackTrace();
        }
    }

    /**
     * Reads the request line and generates the appropriate response object
     * that can later be executed by the connection
     * @return a HttpRequestResponse object to execute
     */
    private HttpRequestResponse readRequest(){
        //steps of the request
        //1. read the request line
        //2. read the body (host necesarry)
        //3. (optional) retrieve the extra body check this by using the parsed input
        String requestLineString = readRequestLine();
        HttpRequestLine requestLine = HttpRequestParser.parseRequestLine(requestLineString);
        String[] requestHeaderString = readRequestHeader();
        HttpRequestHeader requestHeader = HttpRequestParser.parseRequestHeader(requestHeaderString);
        //check if the message has a header
        if(!requestHeader.hasHostHeader()){
            writeErrorMessage(HttpStatusCode.BAD_REQUEST);
        }
        //check if we need to listen for a message body
        String[] messageBody = new String[0];
        if(HttpRequestMethod.hasMessageBody(requestLine.getMethod())){
            messageBody = readMessageBody();
        }
        //get the file system used by the server
        ServerFileSystem fileSystem = this.getServer().getFileSystem();
        //after all the data is read start building the response
        HttpRequestResponse response = RequestResponseFactory.createResponse(requestLine, requestHeader, messageBody, fileSystem);

        return response;
    }

    /**
     * Writes the provided status code to the client
     * @param statusCode th status code that needs to be written
     */
    private void writeErrorMessage(HttpStatusCode statusCode){
        //todo implement the error message
    }

    /**
     * Reads the request line from an http request
     */
    private String readRequestLine(){
        BufferedReader reader = this.getReader();
        //we only need to read one line and parse it
        String requestHeader = null;
        try {
            requestHeader = reader.readLine();
        } catch (IOException e) {
            //got an issue doing IO no idea what went wrong
            e.printStackTrace();
        }

        return requestHeader;
    }

    /**
     * Reads the input stream for the request header
     * @return an array of strings where each entry is a single line of the header
     */
    private String[] readRequestHeader(){
        //read the input until we reach a blank line
        BufferedReader reader = this.getReader();
        String line;
        List<String> headerLines = new ArrayList<>();
        try {
            //read all the lines while adding the lines to the string
            while((line = reader.readLine()).equals("")){
                headerLines.add(line);
            }
        } catch (IOException e) {
            //something went wrong during the transfer print stack
            e.printStackTrace();
        }

        //return the received lines
        return headerLines.toArray(new String[0]);
    }

    /**
     * Reads the input stream for the message body
     * @return an array of strings where each entry equals one line from the request
     */
    private String[] readMessageBody(){
        //todo implement probably needs different implementation for post and put (put needs to be an html page and end with null or </html>
        return null;
    }

    /**
     * Getter for the socket currently used by the server connection
     * @return the connection socket used by this connection
     */
    private Socket getConnectionSocket() {
        return connectionSocket;
    }

    /**
     * Getter for the print stream used to output the data to the client
     * @return the output print stream
     */
    private PrintWriter getPrintWriter() {
        return printWriter;
    }

    /**
     * Getter for the reader that reads the input from the client
     * @return a buffered reader object ready to read input with
     */
    private BufferedReader getReader() {
        return reader;
    }

    /**
     * Getter for the server the connection works with
     * @return
     */
    private Server getServer() {
        return server;
    }

    /**
     * The server that the connection works for, needed to access the file system
     */
    private Server server;

    /**
     * The socket responsible for this connection
     */
    private Socket connectionSocket;

    /**
     * The print output stream of the server, used for printing text to the clients
     * (in this assignment we only need to send text)
     */
    private PrintWriter printWriter;

    /**
     * Buffered reader used for reading the request made by the clients
     */
    private BufferedReader reader;

}
