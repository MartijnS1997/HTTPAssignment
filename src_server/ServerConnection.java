import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        this.server = server;
    }

    @Override
    public void run() {
        //the sequence for the main loop
        //1. read the request
        //2. parse it
        //3. respond using the newly created request
        //4. goto 1.
        initConnection();

        while(true) {
            try {
                HttpRequestResponse response = readRequest();
                response.sendResponse(this.getOutputStream());
            //catch any exceptions thrown by the server that could not be handled in the requests
            } catch (ServerException e){
                writeErrorMessage(e.getStatusCode());
                if(e.getStatusCode().equals(HttpStatusCode.TIMEOUT)){
                    break;
                }
            } catch (Exception e) {
                break;
            }
        }

        terminateConnection();
        System.out.println("Connection terminated");

    }

    /**
     * Initiates the connection by creating the streams necessary for communication with the clients
     */
    private void initConnection(){
        Socket socket = this.getConnectionSocket();

        try {
            InputStream inputStream = socket.getInputStream();
            this.inputStream = new DataInputStream(inputStream);

            this.outputStream = new DataOutputStream(socket.getOutputStream());
//            this.printWriter = new PrintWriter(outputStream);
        }catch(IOException e){
            //something went wrong, dunno what
            e.printStackTrace();
        }
    }

    private void terminateConnection(){
//        PrintWriter writer = this.getPrintWriter();
        DataOutputStream outputStream = this.getOutputStream();
        DataInputStream inputStream = this.getInputStream();
        Socket socket = this.getConnectionSocket();
        try {
            //writer.close();
            outputStream.close();
            inputStream.close();
            socket.close();
        }catch(IOException e){
            //do nothing
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
        //2. read the body (host necessary)
        //3. (optional) retrieve the extra body check this by using the parsed input
        //String requestLineString = readRequestLine();
        String[] requestHeaderLines = new String[0];
        try {
            requestHeaderLines = readRequestHeader();
        } catch (IOException e) {
            throw new ServerException(HttpStatusCode.TIMEOUT);
        }
        System.out.println("The request: " + requestHeaderLines[0]);
        HttpRequestLine requestLine = HttpRequestParser.parseRequestLine(requestHeaderLines[0]);

        System.out.println("Header: " + Arrays.toString(requestHeaderLines));
        HttpRequestHeader requestHeader = HttpRequestParser.parseRequestHeader(requestHeaderLines);
        //check if the message has a header

        if(!requestHeader.hasHostHeader()){
            writeErrorMessage(HttpStatusCode.BAD_REQUEST);
        }
        //check if we need to listen for a message body
        String[] messageBody = new String[0];
        if(HttpRequestMethod.hasMessageBody(requestLine.getMethod())){
            messageBody = readMessageBody(requestHeader);
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
        //PrintWriter writer = this.getPrintWriter();
        DataOutputStream outputStream = this.getOutputStream();
        ResponseHeader header = new ResponseHeader(statusCode);
        header.writeResponseHeader(outputStream);
    }

    /**
     * Reads the request line from an http request
     */
    @Deprecated
    private String readRequestLine(){

//        BufferedReader reader = this.getReader();
//        //we only need to read one line and parse it
//        String requestHeader = null;
//
//        try {
//            //get the start time of the listening process
//            long startTimeMillis = System.currentTimeMillis();
//            //read the input until not null
//            boolean inputStarted = false;
//
//            while(!inputStarted){
//                //get the time passed
//
//                requestHeader = reader.readLine();
//                if(requestHeader == null || reader.equals("")){
//                    //check if the time has exceeded the maximum allowed time
//                    long timePassed = System.currentTimeMillis() - startTimeMillis;
//                    //divide by 1000 to get seconds
//                    if(timePassed/6000f > TIMEOUT_SECONDS){
//                        throw new ServerException(HttpStatusCode.TIMEOUT);
//                    }
//                }else{
//                    inputStarted = true;
//                }
//            }
////            requestHeader = reader.readLine();
//        } catch (IOException e) {
//            //got an issue doing IO no idea what went wrong
//            e.printStackTrace();
//        }
//
//        return requestHeader;
        return null;
    }

    /**
     * Reads the input stream for the request header
     * @return an array of strings where each entry is a single line of the header
     */
    private String[] readRequestHeader() throws IOException {
        DataInputStream inputStream = this.getInputStream();
        //the character used to buffer
        int charRead;
        //the builder where the header is built
        StringBuilder sb = new StringBuilder();
        while (true) {
            sb.append((char) (charRead = inputStream.read()));
            if ((char) charRead == '\r') {            // if we've got a '\r'
                sb.append((char) inputStream.read()); // then write '\n'
                charRead = inputStream.read();        // read the next char;
                if (charRead == '\r') {                  // if it's another '\r'
                    sb.append((char) inputStream.read());// write the '\n'
                    break;
                } else {
                    sb.append((char) charRead);
                }
            }
        }

        //return the received lines
        String[] headersArray = sb.toString().split("\r\n");
        //clean the empty lines
        return headersArray;
//        return Arrays.stream(headersArray).filter(s -> !s.matches("\\R")).collect(Collectors.toList()).toArray(new String[0]);
    }

    static String[] readFileAtRelPath(Path relPath){
        if(relPath.isAbsolute()){
            throw new IllegalArgumentException("The path is not relative");
        }
        Path currentWorkingDir = Paths.get(System.getProperty("user.dir"));
        Path filePath =  Paths.get(currentWorkingDir.toString(), relPath.toString());

        //get the file located at the error path
        File errorPage = new File(filePath.toUri());
        //initialize the error array
        List<String> fileLines = new ArrayList<>();

        //create a stream to read from it
        try {
            BufferedReader reader = new BufferedReader(new FileReader(errorPage));
            //start reading the lines, append them to the response body, keep reading until null
            String line;
            while((line = reader.readLine()) != null) {
                fileLines.add(line);
            }
        } catch (IOException e ) {
            //the file was not found, or there went something wrong with the streams
            //anyway this is a server side error
            throw new ServerException(HttpStatusCode.SERVER_ERROR);
        }

        int bodySize = fileLines.size();
        return fileLines.toArray(new String[bodySize]);
    }

    /**
     * Reads the input stream for the message body
     * @return an array of strings where each entry equals one line from the request
     */
    private String[] readMessageBody(HttpRequestHeader requestHeader){
        //get the content length:
        long bytesToRead = requestHeader.getContentLength();
        //create a buffer:
        //if larger than the cap, split in seperate buffers
        byte buffer[] = bytesToRead >= BUFFER_CAP ? new byte[BUFFER_CAP] : new byte[Math.toIntExact(bytesToRead)];
        //get the input stream
        DataInputStream inputStream = this.getInputStream();
        ByteArrayOutputStream stringStream = new ByteArrayOutputStream();
        int readBytes;
        try {
            while(bytesToRead != 0){

                    readBytes = inputStream.read(buffer);
                    //subtract the read bytes from the download bytes
                    bytesToRead -= readBytes;
                    //add the buffer to a buffer
                    stringStream.write(buffer);
                    //check if the buffer needs to be adjusted
                    if(bytesToRead > buffer.length){
                        //we need to stop exactly at the nb of bytes we need
                        buffer = new byte[Math.toIntExact(bytesToRead)];
                    }
                    //goto start

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //convert the buffer to a string
        String outputString = "";
        try {
            outputString = stringStream.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //split the string on new lines
        String lines[] = outputString.split("\\R");
        return lines;
    }

    /**
     * Getter for the input stream used by the server for reading input
     * @return the input stream used by the server to read input from the clients
     */
    public DataInputStream getInputStream() {
        return inputStream;
    }

    /**
     * Getter for the socket currently used by the server connection
     * @return the connection socket used by this connection
     */
    private Socket getConnectionSocket() {
        return connectionSocket;
    }

//    /**
//     * Getter for the print stream used to output the data to the client
//     * @return the output print stream
//     */
//    @Deprecated
//    private PrintWriter getPrintWriter() {
//        return printWriter;
//    }
//
//    /**
//     * Getter for the reader that reads the input from the client
//     * @return a buffered reader object ready to read input with
//     */
//    @Deprecated
//    private BufferedReader getReader() {
//        return reader;
//    }

    /**
     * Getter for the data output stream of the connection, used for communication with the clients
     * @return the data output stream used by the server for communication with the clients
     */
    private DataOutputStream getOutputStream() {
        return outputStream;
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
//    /**
//     * Writer used to write messages to the client
//     */
//    private PrintWriter writer;

    /**
     * The output stream used to write the request responses to the clients
     */
    private DataOutputStream outputStream;
    /**
     * The input stream used for reading data from the client
     */
    private DataInputStream inputStream;


    /*
    Constants
     */
    private final static int TIMEOUT_SECONDS = 10;

    /**
     * the maximum buffer size for reading message bodies
     */
    private final static int BUFFER_CAP = 8192;
}
