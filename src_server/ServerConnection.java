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
    public ServerConnection(Socket socket){
        this.connectionSocket = socket;
    }

    @Override
    public void run() {
        //the sequence for the main loop
        //1. read the request
        //2. parse it
        //3. respond using the newly created request
        //4. goto 1.

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

    private HttpRequestResponse readRequest(){
        //steps of the request
        //1. read the request line
        //2. read the body (host necesarry)
        //3. (optional) retrieve the extra body check this by using the parsed input

        return null;
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
        return (String[]) headerLines.toArray();
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
